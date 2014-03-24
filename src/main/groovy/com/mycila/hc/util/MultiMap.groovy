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
class MultiMap<K, V> implements Iterable<Map.Entry<K, List<V>>> {

    protected final Map<K, List<V>> map = [:]

    @Override
    Iterator<Map.Entry<K, List<V>>> iterator() {
        map.entrySet().iterator()
    }

    String getString(K key, String join) {
        get(key).join(join)
    }

    Iterator<Map.Entry<K, String>> stringIterator(String join) {
        Iterator<Map.Entry<K, List<V>>> iter = iterator()
        return new Iterator<Map.Entry<K, String>>() {
            @Override
            boolean hasNext() {
                return iter.hasNext()
            }

            @Override
            Map.Entry<K, String> next() {
                Map.Entry<K, List<V>> e = iter.next()
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

    boolean contains(K key) {
        map[key] != null
    }

    void remove(K key) {
        map.remove(key)
    }

    void add(K key, V value) {
        if (value == null) {
            remove(key)
        } else {
            if (!contains(key)) {
                map[key] = []
            }
            map[key] << value
        }
    }

    void add(K key, Collection<V> values) {
        if (!contains(key)) {
            map[key] = []
        }
        map[key].addAll(values)
    }

    void addAll(Map<K, V> map) {
        map.each { k, v -> add(k, v) }
    }

    void put(K key, V value) {
        remove(key)
        add(key, value)
    }

    void putAll(Map<K, V> map) {
        map.each { k, v -> put(k, v) }
    }

    V getFirst(K key) {
        List<V> l = get(key)
        return l == null || l.size() == 0 ? null : l.get(0)
    }

    List<V> get(K key) {
        map[key] ?: []
    }

    // groovy support of: map[key]
    List<V> getAt(K key) {
        return get(key)
    }

    @Override
    String toString() { map }

}
