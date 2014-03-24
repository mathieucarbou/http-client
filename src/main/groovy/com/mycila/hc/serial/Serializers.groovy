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
import com.mycila.hc.HttpForm
import com.mycila.hc.HttpRequest
import com.mycila.hc.io.*

import java.nio.ByteBuffer
import java.nio.file.Path

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-13
 */
class Serializers {

    protected final Deque<Serializer> serializers = new ArrayDeque<>()
    protected final HttpClientConfig config

    Serializers(HttpClientConfig config) {
        this.config = config
        addSerializer new ClosureSerializer(
            type: Path,
            delegate: { HttpClientConfig c, HttpRequest r, Path o -> new PathContentProvider(o, c.maxBufferSize) }
        )
        addSerializer new ClosureSerializer(
            type: File,
            delegate: { HttpClientConfig c, HttpRequest r, File o -> new PathContentProvider(o.toPath(), c.maxBufferSize) }
        )
        addSerializer new ClosureSerializer(
            type: ByteBuffer,
            delegate: { HttpRequest r, ByteBuffer o -> new ByteBufferContentProvider(o) }
        )
        addSerializer new ClosureSerializer(
            type: byte[],
            delegate: { HttpRequest r, byte[] o -> new BytesContentProvider(o) }
        )
        addSerializer new ClosureSerializer(
            type: String,
            delegate: { HttpRequest r, String o -> new BytesContentProvider(o.getBytes(r.headers.contentCharset)) }
        )
        addSerializer new ClosureSerializer(
            type: InputStream,
            delegate: { HttpClientConfig c, HttpRequest r, InputStream o -> new InputStreamContentProvider(o, c.maxBufferSize) }
        )
        addSerializer new ClosureSerializer(
            type: Reader,
            delegate: { HttpClientConfig c, HttpRequest r, Reader o -> new InputStreamContentProvider(new ReaderInputStream(o, r.headers.contentCharset, c.maxBufferSize), c.maxBufferSize) }
        )
        addSerializer new ClosureSerializer(
            contentType: 'application/x-www-form-urlencoded',
            type: HttpForm,
            delegate: { HttpRequest r, HttpForm o -> new BytesContentProvider(o.content.getBytes(r.headers.contentCharset)) }
        )
    }

    Serializers addSerializer(Serializer<?> serializer) {
        if (serializer instanceof SerializerSkeleton) {
            serializer.config = config
        }
        serializers.addFirst(serializer)
        return this
    }

    Serializers addSerializers(Serializer<?>... serializers) {
        for (int i = serializers.length - 1; i >= 0; i--) {
            addSerializer(serializers[i])
        }
        return this
    }

    public <T> Serializer<T> getSerializer(Class<T> type, String contentType) {
        serializers.find { it.supports(type, contentType) } ?: FAILING
    }

    protected static final Serializer<?> FAILING = new Serializer<Object>() {
        @Override
        boolean supports(Class<?> targetType, String ct) { return false }

        @Override
        ContentProvider serialize(HttpRequest request, Object o) {
            throw new UnsupportedOperationException('No serializer found to convert ' + o.class.name + ' into ' + request.headers.contentType)
        }
    }

}
