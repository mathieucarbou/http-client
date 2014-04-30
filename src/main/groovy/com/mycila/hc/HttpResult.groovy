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
import com.mycila.hc.serial.Deserializers

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-12
 */
class HttpResult {

    final HttpRequest request
    final HttpResponse response
    final ContentProvider contentProvider
    protected final Deserializers deserializers

    HttpResult(HttpRequest request, HttpResponse response, Deserializers deserializers = null, ContentProvider contentProvider = null) {
        this.request = request
        this.response = response
        this.contentProvider = contentProvider
        this.deserializers = deserializers
    }

    public <T> T getContent(Class<T> type) {
        if (contentProvider == null || request.responseContentHandling != HttpClientConfig.ResponseContentHandling.BUFFERED) {
            throw new IllegalStateException("Content ignored or streamed only")
        }
        return deserializers.getDeserializer(type, response.headers.contentType).deserialize(this, type)
    }

    @Override
    String toString() { "${request}\n${response}" }
}
