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
package com.mycila.hc

import com.mycila.hc.serial.Deserializers
import com.mycila.hc.serial.Serializers
import com.mycila.hc.util.SameThreadExecutor

import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-13
 */
class HttpClientConfig {

    static enum ResponseContentHandling {
        IGNORED, STREAMED, BUFFERED
    }

    Deserializers deserializers = new Deserializers(this)
    Serializers serializers = new Serializers(this)
    ExecutorService executorService = new SameThreadExecutor()
    Proxy proxy = Proxy.NO_PROXY
    Map<String, Object> settings = [:]

    int backoffMaxRetry = 5
    long backoffInitialDelay = 5 * 1000
    float backoffDelayFactor = 1.5
    ScheduledExecutorService backoffScheduler

    ResponseContentHandling responseContentHandling = ResponseContentHandling.IGNORED
    int maxBufferSize = 8 * 1024
    boolean sslVerification = false
    boolean followRedirects = false
    boolean allowCache = false
    long connectTimeout = 20 * 1000
    long readTimeout = 20 * 1000

    boolean isBackoff() { backoffScheduler != null }

}
