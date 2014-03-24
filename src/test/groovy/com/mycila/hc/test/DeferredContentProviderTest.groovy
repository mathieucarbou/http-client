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
package com.mycila.hc.test

import com.mycila.hc.io.ContentProviderInputStream
import com.mycila.hc.io.DeferredContentProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import java.nio.ByteBuffer

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-28
 */
@RunWith(JUnit4)
class DeferredContentProviderTest extends AbstractTest {

    DeferredContentProvider contentProviderStreamed
    DeferredContentProvider contentProviderBuffered
    Collection<ByteBuffer> buffers = someByteBuffers()

    @Before
    void prepare() {
        // prepare
        contentProviderStreamed = new DeferredContentProvider(-1, false)
        contentProviderBuffered = new DeferredContentProvider(-1, true)
        buffers.each {
            contentProviderStreamed.offer(it)
            contentProviderBuffered.offer(it)
        }
        contentProviderStreamed.close()
        contentProviderBuffered.close()
    }

    @Test
    void test_read_buffers() {
        // read each buffers
        int i = 0
        contentProviderStreamed.each { it.is(buffers[i++]) }
        assert i == buffers.size()

        // cannot read twice in streaming mode
        try {
            contentProviderStreamed.iterator()
            assert false
        } catch (Exception e) {
            assert IllegalStateException.isInstance(e) && e.message == 'stream closed'
        }
    }

    @Test
    void test_read_is() {
        // linked to inputstream
        assert LOREMIPSUM == new ContentProviderInputStream(contentProviderStreamed).text
    }

    @Test
    void test_read_buffered() {
        // can read multiple times
        assert LOREMIPSUM == new ContentProviderInputStream(contentProviderBuffered).text
        assert LOREMIPSUM == new ContentProviderInputStream(contentProviderBuffered).text
    }

}
