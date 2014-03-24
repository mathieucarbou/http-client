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

import com.mycila.hc.io.ByteBufferContentProvider
import com.mycila.hc.io.ContentProviderInputStream
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-28
 */
@RunWith(JUnit4)
class ContentProviderInputStreamTest extends AbstractTest {
    @Test
    void test() {
        ContentProviderInputStream stream = new ContentProviderInputStream(new ByteBufferContentProvider(someByteBuffers()))
        assert LOREMIPSUM == stream.text
    }
}
