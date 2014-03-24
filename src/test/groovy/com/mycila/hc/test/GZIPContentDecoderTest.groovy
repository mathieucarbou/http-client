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

import com.mycila.hc.io.ContentDecoder
import com.mycila.hc.io.GZIPContentDecoder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-28
 */
@RunWith(JUnit4)
class GZIPContentDecoderTest extends AbstractTest {
    @Test
    void test_decode() {
        Collection<ByteBuffer> compressed = someByteBuffers(LOREMIPSUM, true)
        Collection<ByteBuffer> buffers = []
        ContentDecoder decoder = new GZIPContentDecoder(20)
        compressed.each { buffers << decoder.decode(it) }
        println buffers
        String content = ''
        buffers.each {
            String r = StandardCharsets.UTF_8.decode(it).toString()
            println "==> ${r}"
            content += r
        }
        assert LOREMIPSUM == content
    }
}
