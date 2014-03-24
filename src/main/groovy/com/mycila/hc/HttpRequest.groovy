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

import com.mycila.hc.io.ContentProvider

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-12
 */
class HttpRequest {

    HttpMethod method = HttpMethod.GET
    URL url
    ContentProvider contentProvider
    HttpClientConfig.ResponseContentHandling responseContentHandling = HttpClientConfig.ResponseContentHandling.IGNORED

    final HttpListeners listeners = new HttpListeners()
    final HttpHeaders headers = new HttpHeaders()
    final HttpQueryParams queryParams = new HttpQueryParams()

    HttpRequest() {
        headers.put(HttpHeader.USER_AGENT, HttpClient.name)
        headers.put(HttpHeader.CONNECTION, 'close')
        headers.add(HttpHeader.ACCEPT_ENCODING, 'gzip')
    }

    boolean isSecured() { url.protocol == 'https' }

    boolean hasContent() {
        contentProvider != null
    }

    URL getRequestUrl() {
        StringBuilder b = new StringBuilder(url.toString())
        if (!queryParams.empty) b.append('?').append(queryParams.queryString)
        return new URL(b.toString())
    }

    @Override
    String toString() { "> ${method} ${requestUrl}" + (headers.empty ? '' : '\n>  + ' + headers.collect { "${it.key}: ${it.value.join(',')}" }.join('\n>  + ')) }

}
