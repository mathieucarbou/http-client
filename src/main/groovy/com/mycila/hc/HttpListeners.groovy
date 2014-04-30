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

import java.util.concurrent.ConcurrentLinkedDeque

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-26
 */
class HttpListeners implements HttpListener.Headers, HttpListener.Content, HttpListener.Complete, HttpListener.Failure, HttpListener.Abort, HttpListener.Retry {

    protected final Map<Class<HttpListener>, Deque<HttpListener>> listeners = [
        (HttpListener.Headers): new ConcurrentLinkedDeque<HttpListener.Headers>(),
        (HttpListener.Content): new ConcurrentLinkedDeque<HttpListener.Content>(),
        (HttpListener.Complete): new ConcurrentLinkedDeque<HttpListener.Complete>(),
        (HttpListener.Failure): new ConcurrentLinkedDeque<HttpListener.Failure>(),
        (HttpListener.Abort): new ConcurrentLinkedDeque<HttpListener.Abort>(),
        (HttpListener.Retry): new ConcurrentLinkedDeque<HttpListener.Retry>(),
    ]

    void addFirst(HttpListener listener) {
        listeners.keySet().findAll { it.isInstance(listener) }.each { listeners[it].addFirst(listener) }
    }

    void add(HttpListener listener) {
        listeners.keySet().findAll { it.isInstance(listener) }.each { listeners[it].add(listener) }
    }

    void remove(HttpListener listener) {
        listeners.keySet().findAll { it.isInstance(listener) }.each { listeners[it].remove(listener) }
    }

    @Override
    void onHeaders(HttpExchange exchange) {
        Log.trace('[%s] event: onHeaders', null, exchange.request.url)
        listeners[HttpListener.Headers].each {
            if (!exchange.aborted) {
                ((HttpListener.Headers) it).onHeaders(exchange)
            }
        }
    }

    @Override
    void onContent(HttpContent content) {
        Log.trace('[%s] event: onContent', null, content.exchange.request.url)
        listeners[HttpListener.Content].each {
            if (!content.exchange.aborted) {
                ((HttpListener.Content) it).onContent(content)
            }
        }
    }

    @Override
    void onComplete(HttpResult result) {
        Log.trace('[%s] event: onComplete', null, result.request.url)
        listeners[HttpListener.Complete].each { ((HttpListener.Complete) it).onComplete(result) }
    }

    @Override
    void onFailure(HttpException e) {
        Log.trace('[%s] event: onFailure', null, e.request.url)
        listeners[HttpListener.Failure].each { ((HttpListener.Failure) it).onFailure(e) }
    }

    @Override
    void onAbort(HttpExchange exchange) {
        Log.trace('[%s] event: onAbort', null, exchange.request.url)
        listeners[HttpListener.Abort].each { ((HttpListener.Abort) it).onAbort(exchange) }
    }

    @Override
    void onRetry(HttpException e) {
        Log.trace('[%s] event: onRetry', null, e.request.url)
        listeners[HttpListener.Retry].each { ((HttpListener.Retry) it).onRetry(e) }
    }
}
