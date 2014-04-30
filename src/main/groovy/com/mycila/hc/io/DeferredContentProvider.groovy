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

import com.mycila.hc.*

import java.nio.ByteBuffer
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-17
 */
class DeferredContentProvider implements ContentProvider, HttpListener.Content, HttpListener.Failure, HttpListener.Abort {

    protected static final ByteBuffer EOS = ByteBuffer.allocate(0)

    protected volatile HttpException exception
    protected volatile HttpRequest request

    protected final AtomicBoolean filled = new AtomicBoolean()
    protected final Queue<ByteBuffer> memoizer
    protected final Lock lock = new ReentrantLock()
    protected final List<QueueIterator> queues = []

    final long length

    DeferredContentProvider(long length = -1, boolean buffered = false) {
        this.length = length
        memoizer = buffered ? new LinkedList<>() : null
        if (!buffered) {
            queues << new QueueIterator()
        }
    }

    @Override
    Iterator<ByteBuffer> iterator() {
        if (exception != null) throw HttpException.unwrapUnchecked(exception)
        if (memoizer == null) {
            if (queues.empty) throw new IllegalStateException('stream closed')
            return queues.get(0)
        } else {
            lock.lockInterruptibly()
            try {
                QueueIterator queue = new QueueIterator(memoizer)
                queues << queue
                return queue
            } finally {
                lock.unlock()
            }
        }
    }

    @Override
    void close() throws IOException {
        if (filled.compareAndSet(false, true)) {
            if (request) Log.trace('[%s] DeferredContentProvider closing', null, request?.url)
            doOffer(EOS)
        }
    }

    @Override
    void onContent(HttpContent content) {
        this.request = content.exchange.request
        offer(content.buffer)
    }

    @Override
    void onFailure(HttpException e) {
        if (filled.compareAndSet(false, true)) {
            this.exception = e
            doOffer(EOS)
        }
    }

    @Override
    void onAbort(HttpExchange exchange) {
        if (filled.compareAndSet(false, true)) {
            this.exception = HttpException.abort(request)
            doOffer(EOS)
        }
    }

    void offer(ByteBuffer buffer) throws InterruptedException {
        if (filled.get()) {
            throw new IllegalStateException('stream closed')
        }
        if (buffer.hasRemaining()) {
            doOffer(buffer)
        }
    }

    protected void doOffer(ByteBuffer buffer) throws InterruptedException {
        if (request) {
            if (EOS.is(buffer)) {
                Log.trace('[%s] DeferredContentProvider offering EOS', null, request?.url)
            } else {
                Log.trace('[%s] DeferredContentProvider offering %s bytes', null, request?.url, buffer.remaining())
            }
        }
        if (memoizer == null) {
            queues.get(0).offer(buffer)
        } else {
            lock.lockInterruptibly()
            try {
                memoizer.offer(buffer)
                queues.each { it.offer(buffer) }
            } finally {
                lock.unlock()
            }
        }
    }

    protected class QueueIterator implements Iterator<ByteBuffer> {
        final BlockingQueue<ByteBuffer> queue = new LinkedBlockingQueue<ByteBuffer>()
        ByteBuffer next

        QueueIterator(Collection<ByteBuffer> init = []) {
            if(request) Log.trace('[%s] DeferredContentProvider new QueueIterator', null, request?.url)
            queue.addAll(init)
        }

        @Override
        boolean hasNext() {
            if (exception) throw HttpException.unwrapUnchecked(exception)
            if (EOS.is(next)) return false
            return next == null ? advance() : true
        }

        @Override
        ByteBuffer next() {
            if (exception) throw HttpException.unwrapUnchecked(exception)
            if (next == null) advance()
            if (EOS.is(next)) throw new NoSuchElementException()
            ByteBuffer b = next.asReadOnlyBuffer()
            next = null
            return b
        }

        @Override
        void remove() {
            throw new UnsupportedOperationException()
        }

        void offer(ByteBuffer byteBuffer) {
            queue.offer(byteBuffer)
        }

        boolean advance() {
            next = queue.take()
            if (EOS.is(next)) {
                if (memoizer == null) {
                    queues.remove(this)
                } else {
                    lock.lockInterruptibly()
                    try {
                        queues.remove(this)
                    } finally {
                        lock.unlock()
                    }
                }
                return false
            }
            return true
        }
    }

}
