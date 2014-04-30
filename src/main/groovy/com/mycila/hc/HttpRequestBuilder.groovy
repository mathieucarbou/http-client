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

import com.mycila.hc.io.ContentProvider
import com.mycila.hc.io.ContentProviderFactory
import com.mycila.hc.io.DelegatingContentProvider

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-12
 */
class HttpRequestBuilder {

    protected final HttpRequest request
    protected final HttpClient client

    HttpRequestBuilder(HttpClient client) {
        this.client = client
        this.request = new HttpRequest(
            responseContentHandling: client.config.responseContentHandling
        )
    }

    HttpRequestBuilder url(String url) {
        request.url = url == null ? null : new URL(url)
        return this
    }

    HttpRequestBuilder url(URL url) {
        request.url = url
        return this
    }

    HttpRequestBuilder method(HttpMethod method) {
        request.method = method
        return this
    }

    // content

    HttpRequestBuilder form(HttpForm form) {
        method(HttpMethod.POST)
        if (!request.headers.contains(HttpHeader.CONTENT_TYPE)) {
            contentType('application/x-www-form-urlencoded', StandardCharsets.UTF_8)
        }
        content(form)
        return this
    }

    HttpRequestBuilder json(String json) {
        if (!request.headers.contains(HttpHeader.CONTENT_TYPE)) {
            contentType('application/json', StandardCharsets.UTF_8)
        }
        content(json)
        return this
    }

    HttpRequestBuilder contentProvider(ContentProvider contentProvider) {
        request.contentProvider = contentProvider
        return this
    }

    HttpRequestBuilder contentProviderFactory(ContentProviderFactory factory) {
        contentProvider(new DelegatingContentProvider(factory))
    }

    HttpRequestBuilder content(Object content) {
        Class<Object> c = content.getClass()
        contentProviderFactory({ client.config.serializers.getSerializer(c, request.headers.contentType).serialize(request, content) })
    }

    HttpRequestBuilder responseContentIgnored() {
        request.responseContentHandling = HttpClientConfig.ResponseContentHandling.IGNORED
        return this
    }

    HttpRequestBuilder responseContentBuffered() {
        request.responseContentHandling = HttpClientConfig.ResponseContentHandling.BUFFERED
        return this
    }

    HttpRequestBuilder responseContentStreamed() {
        request.responseContentHandling = HttpClientConfig.ResponseContentHandling.STREAMED
        return this
    }

    // query string

    HttpRequestBuilder params(Map<String, Object> params) {
        params.each { k, v -> param(k, v) }
        return this
    }

    HttpRequestBuilder param(String key, Object value) {
        request.queryParams.add(key, value)
        return this
    }

    // headers

    HttpRequestBuilder headers(Map<String, Object> headers) {
        headers.each { k, v -> header(k, v) }
        return this
    }

    HttpRequestBuilder header(String key, Object value) {
        request.headers.put(key, value?.toString())
        return this
    }

    HttpRequestBuilder contentType(String contentType, String charset = null) {
        header(HttpHeader.CONTENT_TYPE, charset == null ? contentType : "${contentType}; charset=${charset}")
    }

    HttpRequestBuilder contentType(String contentType, Charset charset) {
        this.contentType(contentType, charset.name())
    }

    HttpRequestBuilder contentLength(long length) {
        header(HttpHeader.CONTENT_LENGTH, length)
    }

    HttpRequestBuilder contentEncoding(String encoding) {
        header(HttpHeader.CONTENT_ENCODING, encoding)
    }

    HttpRequestBuilder userAgent(String ua) {
        header(HttpHeader.USER_AGENT, ua)
    }

    HttpRequestBuilder acceptLanguage(Locale locale) {
        header(HttpHeader.ACCEPT_LANGUAGE, locale.toString().replaceAll('_', '-'))
    }

    HttpRequestBuilder acceptLanguages(Locale... locales) {
        locales.each { acceptLanguage(it) }
        return this
    }

    HttpRequestBuilder cookie(HttpCookie cookie) {
        header(HttpHeader.COOKIE, cookie.toString().split(";", 1)[0])
    }

    HttpRequestBuilder cookies(HttpCookie... cookies) {
        header(HttpHeader.COOKIE, cookies*.toString()*.split(";", 2).findResults { (it ?: [null]).first() }.join(';'))
    }

    // listeners

    HttpRequestBuilder on(HttpListener listener) {
        request.listeners.add(listener)
        return this
    }

    HttpRequestBuilder onHeaders(HttpListener.Headers listener) {
        return on(listener)
    }

    HttpRequestBuilder onContent(HttpListener.Content listener) {
        return on(listener)
    }

    HttpRequestBuilder onComplete(HttpListener.Complete listener) {
        return on(listener)
    }

    HttpRequestBuilder onFailure(HttpListener.Failure listener) {
        return on(listener)
    }

    HttpRequestBuilder onRetry(HttpListener.Retry listener) {
        return on(listener)
    }

    HttpRequestBuilder onAbort(HttpListener.Abort listener) {
        return on(listener)
    }

    // invokes

    HttpExchange send(HttpListener.Complete listener = null) {
        if (listener) on(listener)
        return client.execute(request)
    }

}
