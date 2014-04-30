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

import com.mycila.hc.serial.Deserializer
import com.mycila.hc.serial.Serializer

import java.util.concurrent.*

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-13
 */
abstract class HttpClientBuilder {

    protected final HttpClientConfig config = new HttpClientConfig()

    HttpClientBuilder addSerializer(Serializer serializer) {
        config.serializers.addSerializer(serializer)
        return this
    }

    HttpClientBuilder addSerializers(Serializer... serializers) {
        config.serializers.addSerializers(serializers)
        return this
    }

    HttpClientBuilder addDeserializer(Deserializer<?> deserializer) {
        config.deserializers.addDeserializer(deserializer)
        return this
    }

    HttpClientBuilder addDeserialziers(Deserializer<?>... deserializers) {
        config.deserializers.addDeserializers(deserializers)
        return this
    }

    HttpClientBuilder proxy(Proxy proxy) {
        config.proxy = proxy
        return this
    }

    HttpClientBuilder executor(ExecutorService executorService) {
        config.executorService = executorService
        return this
    }

    HttpClientBuilder executorPool(int minIdle, int maxSize) {
        config.executorService = new ThreadPoolExecutor(
            minIdle, maxSize,
            1, TimeUnit.MINUTES,
            new SynchronousQueue<Runnable>(),
            Executors.defaultThreadFactory(),
            new RejectedExecutionHandler() {
                @Override
                void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    r.run()
                }
            }
        )
        return this
    }

    HttpClientBuilder enableSSLVerifier() {
        config.sslVerification = true
        return this
    }

    HttpClientBuilder enableRedirect() {
        config.followRedirects = true
        return this
    }

    HttpClientBuilder enableCache() {
        config.allowCache = true
        return this
    }

    HttpClientBuilder bufferSize(int size) {
        config.maxBufferSize = size
        return this
    }

    HttpClientBuilder responseContentIgnored() {
        config.responseContentHandling = HttpClientConfig.ResponseContentHandling.IGNORED
        return this
    }

    HttpClientBuilder responseContentBuffered() {
        config.responseContentHandling = HttpClientConfig.ResponseContentHandling.BUFFERED
        return this
    }

    HttpClientBuilder responseContentStreamed() {
        config.responseContentHandling = HttpClientConfig.ResponseContentHandling.STREAMED
        return this
    }

    HttpClientBuilder connectTimeout(long time, TimeUnit unit) {
        config.connectTimeout = TimeUnit.MILLISECONDS.convert(time, unit)
        return this
    }

    HttpClientBuilder readTimeout(long time, TimeUnit unit) {
        config.readTimeout = TimeUnit.MILLISECONDS.convert(time, unit)
        return this
    }

    HttpClientBuilder enableBackoff(ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)) {
        config.backoffScheduler = scheduler
        return this
    }

    HttpClientBuilder backoffMaxRetry(int count) {
        config.backoffMaxRetry = count
        return this
    }

    HttpClientBuilder backoffInitialDelay(long time, TimeUnit unit) {
        config.backoffInitialDelay = TimeUnit.MILLISECONDS.convert(time, unit)
        return this
    }

    HttpClientBuilder backoffDelayFactor(float factor) {
        config.backoffDelayFactor = factor
        return this
    }

    HttpClientBuilder set(String name, Object value) {
        config.settings.put(name, value)
        return this
    }

    HttpClient build() {
        HttpClient client = newInstance()
        client.init()
        return client
    }

    protected abstract HttpClient newInstance()
}
