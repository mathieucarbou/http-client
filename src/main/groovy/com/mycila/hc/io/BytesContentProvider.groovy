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
 * @date 2014-02-17
 */
class BytesContentProvider implements ContentProvider {

    protected final List<byte[]> buffers
    protected final long length

    BytesContentProvider(byte[] buffer) {
        this([buffer])
    }

    BytesContentProvider(List<byte[]> buffers) {
        this.buffers = buffers
        this.length = buffers.sum(0L, { byte[] b -> b.length }) as long
    }

    @Override
    void close() throws IOException {

    }

    @Override
    long getLength() {
        return length
    }

    @Override
    Iterator<ByteBuffer> iterator() {
        new Iterator<ByteBuffer>() {
            int index

            @Override
            boolean hasNext() {
                index < buffers.size()
            }

            @Override
            ByteBuffer next() {
                try {
                    return ByteBuffer.wrap(buffers[index++])
                }
                catch (ArrayIndexOutOfBoundsException ignored) {
                    throw new NoSuchElementException()
                }
            }

            @Override
            void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}