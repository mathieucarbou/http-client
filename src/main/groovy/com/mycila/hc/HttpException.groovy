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

import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-12
 */
class HttpException extends RuntimeException {

    final HttpRequest request
    // if backoff enabled, can retry
    boolean retryable = true

    protected HttpException(HttpRequest request, Throwable cause) {
        super(cause.message, cause)
        this.request = request
        setStackTrace(cause.stackTrace)
    }

    void doNotRetry () {
        retryable = false
    }

    @Override
    String toString() {
        "${cause.class.simpleName}: ${message}\n${request}"
    }

    static HttpException abort(HttpRequest request) {
        wrap(new CancellationException('request aborted'), request)
    }

    static RuntimeException unwrapUnchecked(Throwable o) {
        HttpRequest request = null
        if (o instanceof ExecutionException) o = o.cause
        if (o instanceof HttpException) {
            o = o.cause
            request = ((HttpException) o).request
        }
        return o instanceof RuntimeException ? (RuntimeException) o : wrap(o, request)
    }

    static Throwable unwrap(Throwable o) {
        if (o instanceof ExecutionException) o = o.cause
        if (o instanceof HttpException) o = o.cause
        return o
    }

    static HttpException wrap(Throwable e, HttpRequest request = null) {
        if (e instanceof ExecutionException) e = e.cause
        return e instanceof HttpException ? e : new HttpException(request, e)
    }

}
