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
package com.mycila.hc.serial

import com.mycila.hc.HttpClientConfig
import com.mycila.hc.HttpResult
import com.mycila.hc.io.ContentProviderInputStream

import java.nio.ByteBuffer

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-13
 */
class Deserializers {

    protected final Deque<Deserializer> deserializers = new ArrayDeque<>()
    protected final HttpClientConfig config

    Deserializers(HttpClientConfig config) {
        this.config = config
        addDeserializer new ClosureDeserializer(
            type: String,
            delegate: { HttpResult r -> new String(new ContentProviderInputStream(r.contentProvider).bytes, r.response.headers.contentCharset) }
        )
        addDeserializer new ClosureDeserializer(
            type: byte[],
            delegate: { HttpResult r -> new ContentProviderInputStream(r.contentProvider).bytes }
        )
        addDeserializer new ClosureDeserializer(
            type: ByteBuffer,
            delegate: { HttpResult r -> ByteBuffer.wrap(new ContentProviderInputStream(r.contentProvider).bytes) }
        )
        addDeserializer new ClosureDeserializer(
            type: InputStream,
            delegate: { HttpResult r -> new ContentProviderInputStream(r.contentProvider) }
        )
        addDeserializer new ClosureDeserializer(
            type: Reader,
            delegate: { HttpResult r -> new InputStreamReader(new ContentProviderInputStream(r.contentProvider), r.response.headers.contentCharset) }
        )
    }

    Deserializers addDeserializer(Deserializer<?> serializer) {
        if (serializer instanceof DeserializerSkeleton) {
            serializer.config = config
        }
        deserializers.addFirst(serializer)
        return this
    }

    Deserializers addDeserializers(Deserializer<?>... deserializers) {
        for (int i = deserializers.length - 1; i >= 0; i--) {
            addDeserializer(deserializers[i])
        }
        return this
    }

    public <T> Deserializer<T> getDeserializer(Class<? super T> targetType, String contentType) {
        deserializers.find { it.supports(targetType, contentType) } ?: FAILING
    }

    protected static final Deserializer<?> FAILING = new Deserializer<Object>() {
        @Override
        boolean supports(Class<?> targetType, String ct) { return false }

        @Override
        Object deserialize(HttpResult result, Class<Object> tt) {
            throw new UnsupportedOperationException('No serial found to convert ' + result.response.headers.contentType + ' into ' + tt.name)
        }
    }

}
