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
package com.mycila.hc.io

import java.nio.ByteBuffer

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-17
 */
class ByteBufferContentProvider implements ContentProvider {

    protected final List<ByteBuffer> buffers
    protected final long length

    ByteBufferContentProvider(ByteBuffer buffer) {
        this([buffer])
    }

    ByteBufferContentProvider(Collection<ByteBuffer> buffers) {
        this.buffers = buffers as List
        this.length = buffers.sum(0L, { ByteBuffer b -> b.remaining() }) as long
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
                    ByteBuffer buffer = buffers[index]
                    buffers[index] = buffer.slice()
                    index++
                    return buffer
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