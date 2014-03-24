/**
 * Copyright (C) 2013 Mycila (mathieu@mycila.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.hc

import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-03-01
 */
class RetryableCallable implements Callable<HttpResult> {

    protected final Callable<HttpResult> delegate
    protected final HttpClientConfig config

    protected long retryCount
    protected long retryDelay

    protected HttpExchange exchange

    RetryableCallable(HttpClientConfig config, Callable<HttpResult> delegate) {
        this.delegate = delegate
        this.config = config
        this.retryCount = 0
        this.retryDelay = config.backoffInitialDelay
    }

    @Override
    HttpResult call() throws Exception {
        // wrap the runnable because we are using #runAndReset() which does not set the result.
        // So we set it when the call succeeds.
        try {
            // Call to #set() changes the state to RAN and calls the #done() method. #runAndReset() will return false.
            exchange.set(delegate.call())
        } catch (Throwable e) {
            // capture any throwable so that #runAndReset() can set back the state to READY and returns true.
            // If the retry count reached the maximum retry count, then stop by setting the exception: it will have
            // the effect of changing the state to RAN and #runAndReset() will return false
            if (!config.backoff || retryCount >= config.backoffMaxRetry) {
                Log.trace('[%s] backoff: no retry left', null, exchange.request.url)
                exchange.setException(e)
            } else {
                HttpException exception = HttpException.wrap(e, exchange.request)
                exchange.request.listeners.onRetry(exception)
                if (!exception.retryable) {
                    Log.trace('[%s] backoff: retry prevented', null, exchange.request.url)
                    exchange.setException(e)
                } else {
                    Log.trace('[%s] backoff: will retry', null, exchange.request.url)
                }
            }
        }
        return null // #runAndReset() does not handle any return
    }

    void retry() {
        retryDelay = retryDelay * Math.pow(config.backoffDelayFactor, retryCount)
        retryCount++
        Log.trace('[%s] backoff: retrying (%s/%s) in %s ms', null, exchange.request.url, retryCount, config.backoffMaxRetry, retryDelay)
        // schedule a task that will run again this future in the async pool
        config.backoffScheduler.schedule(new Runnable() {
            @Override
            void run() {
                config.executorService.execute(exchange)
            }
        }, retryDelay, TimeUnit.MILLISECONDS)
    }

}
