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

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-18
 */
class HttpForm {

    protected final HttpQueryParams params = new HttpQueryParams()

    HttpForm() {
    }

    HttpForm(Map<String, Object> parameters) {
        params.putAll(parameters)
    }

    HttpForm params(Map<String, Object> parameters) {
        params.putAll(parameters)
        return this
    }

    HttpForm param(String key, Object value) {
        params.put(key, value)
        return this
    }

    String getContent() {
        params.queryString
    }

    @Override
    String toString() {
        content
    }

}