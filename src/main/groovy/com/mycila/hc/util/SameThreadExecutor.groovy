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
package com.mycila.hc.util

import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-12
 */
class SameThreadExecutor extends AbstractExecutorService {

    protected final AtomicBoolean closed = new AtomicBoolean(false)

    @Override
    void shutdown() { closed.set(true) }

    @Override
    List<Runnable> shutdownNow() { return [] }

    @Override
    boolean isShutdown() { closed.get() }

    @Override
    boolean isTerminated() { closed.get() }

    @Override
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException { terminated }

    @Override
    void execute(Runnable command) {
        command.run()
    }
}
