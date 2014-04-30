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

import com.mycila.hc.util.MultiMap

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.text.DateFormat

/**
 * @author Mathieu Carbou  = mathieu.carbou@gmail.com)
 * @date 2014-02-13
 */
class HttpHeaders {

    @Delegate
    private final MultiMap<String> headers = new MultiMap<>(true)

    boolean isChunked() {
        'chunked'.equalsIgnoreCase(getFirst(HttpHeader.TRANSFER_ENCODING))
    }

    long getContentLength() {
        String l = getFirst(HttpHeader.CONTENT_LENGTH)
        return l == null ? -1 : Long.parseLong(l)
    }

    String getContentType() {
        String contentType = getFirst(HttpHeader.CONTENT_TYPE)
        if (contentType != null) {
            int index = contentType.toLowerCase(Locale.ENGLISH).indexOf(";")
            return index > 0 ? contentType.substring(0, index).trim() : contentType
        }
        return null
    }

    Charset getContentCharset() {
        getContentCharset(StandardCharsets.UTF_8)
    }

    Charset getContentCharset(String defaultCharset) {
        getContentCharset(Charset.forName(defaultCharset))
    }

    Charset getContentCharset(Charset defaultCharset) {
        String contentType = getFirst(HttpHeader.CONTENT_TYPE)
        if (contentType != null) {
            String charset = "charset="
            int index = contentType.toLowerCase(Locale.ENGLISH).indexOf(charset)
            if (index > 0) {
                String encoding = contentType.substring(index + charset.length())
                // Sometimes charsets arrive with an ending semicolon
                index = encoding.indexOf(';')
                if (index > 0)
                    encoding = encoding.substring(0, index)
                return Charset.forName(encoding)
            }
        }
        return defaultCharset
    }

    Collection<HttpCookie> getCookies() {
        HttpCookie.parse(getFirst(HttpHeader.SET_COOKIE))
    }

    Date getDateHeader(String name) {
        String dateString = getFirst(name)
        if (dateString) {
            if (dateString.indexOf("GMT") == -1) {
                dateString = dateString + " GMT"
            }
            try {
                return DateFormat.instance.parse(dateString)
            } catch (Exception ignored) {
            }
        }
        return null
    }

    int getIntHeader(String name, int deflt) {
        String value = getFirst(name)
        return value == null ? deflt : Integer.parseInt(value)
    }

    long getLongHeader(String name, long deflt) {
        String value = getFirst(name)
        return value == null ? deflt : Long.parseLong(value)
    }

    @Override
    String toString() {
        headers.collect { "${it.key}: ${it.value.join(',')}" }.join('\n')
    }

}
