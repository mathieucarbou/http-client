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
package com.mycila.hc.util

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-16
 */
class MultiMap<V> implements Iterable<Map.Entry<String, List<V>>> {

    protected final Map<String, List<V>> map = [:]
    private final boolean ignoreCase

    MultiMap(boolean ignoreCase = false) {
        this.ignoreCase = ignoreCase
    }

    @Override
    Iterator<Map.Entry<String, List<V>>> iterator() {
        map.entrySet().iterator()
    }

    String getString(String key, String join) {
        get(key).join(join)
    }

    Iterator<Map.Entry<String, String>> stringIterator(String join) {
        Iterator<Map.Entry<String, List<V>>> iter = iterator()
        return new Iterator<Map.Entry<String, String>>() {
            @Override
            boolean hasNext() {
                return iter.hasNext()
            }

            @Override
            Map.Entry<String, String> next() {
                Map.Entry<String, List<V>> e = iter.next()
                return new MapEntry(e.key, e.value.join(join))
            }

            @Override
            void remove() {
                iter.remove()
            }
        }
    }

    boolean isEmpty() {
        map.isEmpty()
    }

    int size() {
        map.size()
    }

    boolean contains(String key) {
        if(ignoreCase) key = key.toLowerCase()
        map[key] != null
    }

    void remove(String key) {
        if(ignoreCase) key = key.toLowerCase()
        map.remove(key)
    }

    void add(String key, V value) {
        if(ignoreCase) key = key.toLowerCase()
        if (value == null) {
            remove(key)
        } else {
            if (!contains(key)) {
                map[key] = []
            }
            map[key] << value
        }
    }

    void add(String key, Collection<V> values) {
        if(ignoreCase) key = key.toLowerCase()
        if (!contains(key)) {
            map[key] = []
        }
        map[key].addAll(values)
    }

    void addAll(Map<String, V> map) {
        map.each { k, v -> add(k, v) }
    }

    void put(String key, V value) {
        remove(key)
        add(key, value)
    }

    void putAll(Map<String, V> map) {
        map.each { k, v -> put(k, v) }
    }

    V getFirst(String key) {
        List<V> l = get(key)
        return l == null || l.size() == 0 ? null : l.get(0)
    }

    List<V> get(String key) {
        if(ignoreCase) key = key.toLowerCase()
        map[key] ?: []
    }

    // groovy support of: map[key]
    List<V> getAt(String key) {
        return get(key)
    }

    @Override
    String toString() { map }

}
