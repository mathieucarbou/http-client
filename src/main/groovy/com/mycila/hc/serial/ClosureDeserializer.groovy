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
package com.mycila.hc.serial

import com.mycila.hc.HttpResult

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-24
 */
class ClosureDeserializer<T> extends DeserializerSkeleton<T> {

    protected Closure<T> delegate

    void setDelegate(Closure<T> delegate) {
        this.delegate = delegate.maximumNumberOfParameters > 0 && delegate.parameterTypes[0].isInstance(config) ? delegate.curry(config) : delegate
    }

    @Override
    T deserialize(HttpResult result, Class<T> targetType) {
        targetType.cast(delegate(result))
    }

}
