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

import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-03-01
 */
class Log {

    private static final Logger LOGGER = Logger.getLogger(HttpClient.name)
    private static final boolean TRACE = LOGGER.isLoggable(Level.FINEST)

    static void trace(String message, Throwable error = null, Object... args = null) {
        if (TRACE) {
            if (error) {
                if (args) {
                    LOGGER.log(Level.FINEST, String.format(message, args), error)
                } else {
                    LOGGER.log(Level.FINEST, message, error)
                }
            } else {
                if (args) {
                    LOGGER.log(Level.FINEST, String.format(message, args))
                } else {
                    LOGGER.log(Level.FINEST, message)
                }
            }
        }
    }

}
