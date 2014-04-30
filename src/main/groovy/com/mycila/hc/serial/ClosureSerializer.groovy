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

import com.mycila.hc.HttpRequest
import com.mycila.hc.io.ContentProvider

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-24
 */
class ClosureSerializer<T> extends SerializerSkeleton<T> {

    protected Closure<ContentProvider> delegate

    void setDelegate(Closure<ContentProvider> delegate) {
        this.delegate = delegate.maximumNumberOfParameters > 0 && delegate.parameterTypes[0].isInstance(config) ? delegate.curry(config) : delegate
    }

    @Override
    ContentProvider serialize(HttpRequest request, T o) {
        delegate(request, o)
    }
}
