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

import com.mycila.hc.transport.JdkHttpClientBuilder

import java.util.concurrent.Callable

abstract class HttpClient implements Closeable {

    protected HttpClientConfig config

    static HttpClientBuilder newBuilder(Class<? extends HttpClientBuilder> builderType = null) {
        if (builderType) {
            return builderType.newInstance()
        }
        try {
            return ServiceLoader.load(HttpClientBuilder).iterator().next()
        } catch (NoSuchElementException ignored) {
        }
        return new JdkHttpClientBuilder()
    }

    HttpRequestBuilder newRequest(String url = null) {
        new HttpRequestBuilder(this).url((String) url)
    }

    HttpRequestBuilder newRequest(URL url) {
        new HttpRequestBuilder(this).url(url)
    }

    void init() {
    }

    @Override
    void close() {
        config.backoffScheduler?.shutdown()
        config.executorService?.shutdown()
        config.backoffScheduler = null
        config.executorService = null
    }

    protected HttpExchange execute(HttpRequest request) {
        HttpExchange exchange = null
        Callable<HttpResult> task = {
            if (!request.headers.contains(HttpHeader.HOST)) {
                URL url = request.requestUrl
                String host = url.host
                if (url.port != -1 && !(url.protocol == 'http' && url.port == 80 || url.protocol == 'https' && url.port == 443)) {
                    host = host + ':' + url.port
                }
                request.headers.put(HttpHeader.HOST, host)
            }
            if (request.hasContent()) {
                long length = request.contentProvider.length
                if (length >= 0) {
                    if (!request.headers.contains(HttpHeader.CONTENT_LENGTH)) {
                        request.headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(request.contentProvider.length))
                    }
                } else {
                    if (!request.headers.contains(HttpHeader.TRANSFER_ENCODING)) {
                        request.headers.put(HttpHeader.TRANSFER_ENCODING, 'chunked')
                    }
                }
            }
            return send(exchange)
        }
        exchange = new HttpExchange(request, config.backoff ? new RetryableCallable(config, task) : task)
        config.executorService.execute(exchange)
        return exchange
    }

    protected abstract HttpResult send(HttpExchange exchange) throws Exception

}
