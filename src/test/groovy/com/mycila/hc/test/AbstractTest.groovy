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

import org.junit.After
import org.junit.Rule
import org.junit.rules.ExpectedException
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler

import java.nio.ByteBuffer
import java.util.logging.LogManager
import java.util.zip.GZIPOutputStream

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-03-02
 */
abstract class AbstractTest {

    static {
        LogManager.logManager.reset()
        SLF4JBridgeHandler.install()
        LoggerFactory.ILoggerFactory
    }

    private ServerSocket serverSocket
    protected int serverPort

    @Rule public ExpectedException error = ExpectedException.none()

    @After
    void cleanup() {
        serverSocket?.close()
    }

    int openPort() {
        while (true) {
            serverPort = new Random().nextInt(10000) + 1024
            try {
                serverSocket = new ServerSocket(serverPort)
                return serverPort
            } catch (IOException ignored) {
            }
        }
    }

    static final String LOREMIPSUM = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. ' +
        'Phasellus elementum tristique mi, non dictum erat molestie vel. ' +
        'Sed dictum nulla eu accumsan tristique. Cras ac ante nulla. ' +
        'Cras suscipit ante eu accumsan faucibus. Suspendisse eget nunc at sem gravida viverra nec id dolor. ' +
        'Aliquam eleifend et ligula non aliquet. Phasellus et mi libero. Nunc eu eleifend nisi. ' +
        'Interdum et malesuada fames ac ante ipsum primis in faucibus. ' +
        'Sed nisi tortor, eleifend ut placerat quis, ornare et nulla. ' +
        'Aliquam consectetur, dui ac congue commodo, tellus nisl laoreet tortor, sed dictum quam lorem sed mi.'

    static List<ByteBuffer> someByteBuffers(String from = LOREMIPSUM, boolean gzip = false) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        OutputStream out = gzip ? new GZIPOutputStream(baos) : baos
        out.write(from.bytes)
        out.close()
        byte[] bytes = baos.toByteArray()
        List<ByteBuffer> buffers = []
        for (int i = 0; i < bytes.length; i += 16) {
            buffers << ByteBuffer.wrap(bytes, i, Math.min(16, bytes.length - i))
        }
        return buffers
    }

}
