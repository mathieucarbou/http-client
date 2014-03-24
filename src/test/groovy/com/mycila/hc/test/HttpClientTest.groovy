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

import com.mycila.hc.HttpClient
import com.mycila.hc.HttpListener
import com.mycila.hc.HttpMethod
import com.mycila.hc.serial.GroovyJsonDeserializer
import com.mycila.hc.serial.GroovyJsonSerializer
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import java.util.concurrent.TimeUnit

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-03-02
 */
@RunWith(JUnit4)
class HttpClientTest extends AbstractTest {

    @Test
    void error_UnknownHostException() {
        error.expect(UnknownHostException)
        HttpClient.newBuilder()
            .build()
            .newRequest()
            .onFailure({ println "onFailure: ${it.message}" })
            .url('https://INEXISTING.mycila.com/')
            .send().httpResult
    }

    @Test(timeout = 2500L)
    void error_ConnectException() {
        error.expect(ConnectException)
        HttpClient.newBuilder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .build()
            .newRequest()
            .url('http://127.0.0.1:999')
            .send().httpResult
    }

    @Test(timeout = 2500L)
    void error_SocketTimeoutException() {
        error.expect(SocketTimeoutException)
        HttpClient.newBuilder()
            .readTimeout(2, TimeUnit.SECONDS)
            .build()
            .newRequest()
            .url("http://127.0.0.1:${openPort()}")
            .send().httpResult
    }

    @Test
    void error_content_ignored() {
        error.expect(IllegalStateException)
        error.expectMessage('Content ignored or streamed only')
        HttpClient.newBuilder()
            .build()
            .newRequest()
            .url("https://api-stg.guestful.com/api/availabilities")
            .send().httpResult.getContent(String)
    }

    @Test
    void error_content_streamed() {
        error.expect(IllegalStateException)
        error.expectMessage('Content ignored or streamed only')
        HttpClient.newBuilder()
            .build()
            .newRequest()
            .responseContentStreamed()
            .url("https://api-stg.guestful.com/api/availabilities")
            .send().httpResult.getContent(String)
    }

    @Test
    void content_ignored_by_default() {
        HttpClient.newBuilder()
            .build()
            .newRequest()
            .url("https://api-stg.guestful.com/api/availabilities")
            .send().httpResult
    }

    @Test(timeout = 10000L)
    void backoff() {
        error.expect(SocketTimeoutException)
        HttpClient.newBuilder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .enableBackoff()
            .backoffInitialDelay(1, TimeUnit.SECONDS)
            .backoffMaxRetry(2)
            .build()
            .newRequest()
            .onRetry({ println "onRetry:\n${it}" })
            .onFailure({ println "onFailure: ${it.message}" })
            .url("http://127.0.0.1:${openPort()}")
            .send().httpResult
    }

    @Test
    void http_call() {
        println HttpClient.newBuilder()
            .addSerializer(new GroovyJsonSerializer())
            .addDeserializer(new GroovyJsonDeserializer())
            .bufferSize(20)
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .build()
            .newRequest()
            .url('https://api-dev.guestful.com/api/profile/login')
            .method(HttpMethod.POST)
            .responseContentBuffered()
            .contentType('application/json')
            .header('X-Token', 'RUm5pH0AkurSwBVcEEauvw')
            .content([email: 'mathieu@guestful.com', password: 'password1'])
            .onHeaders({ println "onHeaders:\n$it" } as HttpListener.Headers)
            .onContent({ println "onContent:\n$it" } as HttpListener.Content)
            .onComplete({ println "onComplete:\n$it\n${it.getContent(String)}" } as HttpListener.Complete)
            .send()
            .httpResult.getContent(Map)
    }

}
