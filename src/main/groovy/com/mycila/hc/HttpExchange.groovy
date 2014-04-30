/**
 * Copyright (C) 2014 Mycila (mathieu@mycila.com)
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
import java.util.concurrent.CancellationException
import java.util.concurrent.FutureTask

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-17
 */
class HttpExchange extends FutureTask<HttpResult> {

    protected final RetryableCallable retryableCallable

    final HttpRequest request
    final HttpResponse response

    HttpExchange(HttpRequest request, Callable<HttpResult> c) {
        super(c)
        this.request = request
        this.response = new HttpResponse()
        if (c instanceof RetryableCallable) {
            this.retryableCallable = (RetryableCallable) c
            this.retryableCallable.exchange = this
        } else {
            this.retryableCallable = null
        }
    }

    @Override
    void run() {
        if (retryableCallable == null) {
            super.run()
        } else {
            if (runAndReset()) {
                // true if runnable ran and state came back to READY (=> can be re-executed).
                // this case happens if we captured an exception an we have to retry
                retryableCallable.retry()
            } else {
                // exception occured and maximum retry reached or result set. Do not retry
            }
        }
    }

    @Override
    protected void setException(Throwable t) {
        super.setException(t)
    }

    @Override
    protected void set(HttpResult httpResult) {
        super.set(httpResult)
    }

    void abort() {
        Log.trace('[%s] aborting...', null, request.url)
        cancel(true)
    }

    @Override
    boolean cancel(boolean mayInterruptIfRunning) {
        Log.trace('[%s] aborting...', null, request.url)
        return super.cancel(mayInterruptIfRunning)
    }

    boolean isAborted() {
        cancelled
    }

    HttpResult getHttpResult() {
        try {
            return get()
        } catch (Throwable e) {
            throw HttpException.unwrap(e.cause)
        }
    }

    @Override
    protected void done() {
        Log.trace('[%s] done()', null, request.url)
        HttpResult result = null
        try {
            result = httpResult
        } catch (CancellationException ignored) {
            request.listeners.onAbort(this)
        } catch (InterruptedException ignored) {
            request.listeners.onAbort(this)
        } catch (Exception e) {
            request.listeners.onFailure(HttpException.wrap(e, request))
        }
        if (result != null) {
            request.listeners.onComplete(result)
        }
    }

    @Override
    String toString() { "${request}\n${response}" }

}
