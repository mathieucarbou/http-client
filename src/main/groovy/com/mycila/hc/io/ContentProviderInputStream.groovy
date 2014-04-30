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
package com.mycila.hc.io

import java.nio.ByteBuffer

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-13
 */
class ContentProviderInputStream extends InputStream {

    protected ContentProvider contentProvider
    protected Iterator<ByteBuffer> iterator
    protected ByteBuffer current

    ContentProviderInputStream(ContentProvider contentProvider) {
        this.contentProvider = contentProvider
        this.iterator = contentProvider.iterator()
    }

    @Override
    int available() throws IOException {
        return current?.remaining() ?: 0
    }

    @Override
    int read() throws IOException {
        if (current?.hasRemaining()) {
            return current.get() & 0xFF
        }
        while (iterator.hasNext() && !current?.hasRemaining()) {
            current = iterator.next()
        }
        return current?.hasRemaining() ? current.get() & 0xFF : -1
    }

    @Override
    int read(byte[] bytes, int off, int len) throws IOException {
        if (current?.hasRemaining()) {
            len = Math.min(len, current.remaining())
            current.get(bytes, off, len)
            return len
        }
        while (iterator?.hasNext() && !current?.hasRemaining()) {
            current = iterator.next()
        }
        if (current?.hasRemaining()) {
            len = Math.min(len, current.remaining())
            current.get(bytes, off, len)
            return len
        } else {
            return -1
        }
    }

    @Override
    void close() throws IOException {
        contentProvider = null
        iterator = null
        current = null
    }

}
