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
package com.mycila.hc

interface HttpListener {

    interface Headers extends HttpListener {
        void onHeaders(HttpExchange exchange)
    }

    interface Content extends HttpListener {
        void onContent(HttpContent content)
    }

    interface Complete extends HttpListener {
        void onComplete(HttpResult result)
    }

    interface Failure extends HttpListener {
        void onFailure(HttpException e)
    }

    interface Abort extends HttpListener {
        void onAbort(HttpExchange exchange)
    }

    interface Retry extends HttpListener {
        void onRetry(HttpException e)
    }

}
