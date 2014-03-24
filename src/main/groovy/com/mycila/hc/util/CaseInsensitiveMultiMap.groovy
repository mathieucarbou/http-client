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
package com.mycila.hc.util

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-16
 */
class CaseInsensitiveMultiMap<V> extends MultiMap<String, V> {

    @Override
    List<V> get(String key) {
        super.get(key.toLowerCase())
    }

    @Override
    boolean contains(String key) {
        super.contains(key.toLowerCase())
    }

    @Override
    void remove(String key) {
        super.remove(key.toLowerCase())
    }

    @Override
    void add(String key, V value) {
        super.add(key.toLowerCase(), value)
    }

    @Override
    void add(String key, Collection<V> values) {
        super.add(key.toLowerCase(), values)
    }

}
