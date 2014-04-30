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

import com.mycila.hc.io.ByteBufferInputStream

import java.nio.ByteBuffer

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-12
 */
class HttpContent {

    final HttpExchange exchange
    final ByteBuffer buffer

    HttpContent(HttpExchange exchange, ByteBuffer buffer) {
        this.exchange = exchange
        this.buffer = buffer
    }

    InputStream getInputStream() {
        new ByteBufferInputStream(buffer.asReadOnlyBuffer())
    }

    Reader getReader() {
        new InputStreamReader(inputStream, exchange.response.headers.contentCharset)
    }

    byte[] getBytes() {
        ByteBuffer bb = buffer.asReadOnlyBuffer()
        byte[] bytes = new byte[bb.remaining()]
        bb.get(bytes, 0, bytes.length);
        return bytes
    }

    String getText() {
        new String(bytes, exchange.response.headers.contentCharset)
    }

    @Override
    String toString() {
        exchange.toString() + '\n' + text
    }

    boolean isEmpty() {
        buffer.hasRemaining()
    }

}
