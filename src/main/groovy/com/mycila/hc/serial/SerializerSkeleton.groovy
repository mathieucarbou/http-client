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
package com.mycila.hc.serial

import com.mycila.hc.HttpClientConfig

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-24
 */
abstract class SerializerSkeleton<T> implements Serializer<T> {

    HttpClientConfig config
    Collection<Class<?>> types = []
    Collection<String> contentTypes = []

    void setType(Class<?> type) {
        types << type
    }

    void setContentType(String contentType) {
        contentTypes << contentType
    }

    @Override
    boolean supports(Class<?> targetType, String contentType) {
        (contentTypes.empty || contentType in contentTypes) && (types.empty || types.find { it.isAssignableFrom(targetType) })
    }

    @Override
    String toString() { "Serializer(${types*.simpleName} => ${contentTypes})" }

}
