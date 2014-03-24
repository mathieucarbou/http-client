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
import java.nio.channels.SeekableByteChannel
import java.nio.file.*

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-17
 */
class PathContentProvider implements ContentProvider {

    protected final Path filePath
    protected final long fileSize
    protected final int bufferSize

    PathContentProvider(Path filePath, int bufferSize) throws IOException {
        if (!Files.isRegularFile(filePath))
            throw new NoSuchFileException(filePath.toString())
        if (!Files.isReadable(filePath))
            throw new AccessDeniedException(filePath.toString())
        this.filePath = filePath
        this.fileSize = Files.size(filePath)
        this.bufferSize = bufferSize
    }

    @Override
    void close() throws IOException {

    }

    @Override
    public long getLength() {
        return fileSize
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return new PathIterator()
    }

    protected class PathIterator implements Iterator<ByteBuffer>, Closeable {
        protected final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize)
        protected SeekableByteChannel channel
        protected long position

        @Override
        public boolean hasNext() {
            return position < getLength()
        }

        @Override
        public ByteBuffer next() {
            try {
                if (channel == null) {
                    channel = Files.newByteChannel(filePath, StandardOpenOption.READ)
                }
                buffer.clear()
                int read = channel.read(buffer)
                if (read < 0)
                    throw new NoSuchElementException()
                position += read
                if (!hasNext())
                    close()
                buffer.flip()
                return buffer
            }
            catch (NoSuchElementException x) {
                close()
                throw x
            }
            catch (Exception x) {
                close()
                throw (NoSuchElementException) new NoSuchElementException().initCause(x)
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException()
        }

        @Override
        public void close() {
            try {
                if (channel != null)
                    channel.close()
            }
            catch (Exception ignored) {
            }
        }
    }
}
