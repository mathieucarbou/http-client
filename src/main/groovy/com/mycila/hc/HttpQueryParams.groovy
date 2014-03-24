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

import com.mycila.hc.util.MultiMap

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-16
 */
class HttpQueryParams {

    @Delegate
    private final MultiMap<String, Object> params = new MultiMap<>()

    String getQueryString() {
        params.collect{ Map.Entry<String, List<Object>> p ->
            String encK = URLEncoder.encode(p.key, 'UTF-8')
            return (p.value ?: ['']).collect { Object v -> "${encK}=${URLEncoder.encode(String.valueOf(v), 'UTF-8')}" }
        }.flatten().join('&')
    }

    @Override
    String toString() { getQueryString() }

}
