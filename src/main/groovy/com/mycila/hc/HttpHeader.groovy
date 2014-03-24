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
 * @author Mathieu Carbou  = mathieu.carbou@gmail.com)
 * @date 2014-02-13
 */
class HttpHeader {

    /**
     * General Fields.
     */
    static final String CONNECTION = "Connection"
    static final String CACHE_CONTROL = "Cache-Control"
    static final String DATE = "Date"
    static final String PRAGMA = "Pragma"
    static final String PROXY_CONNECTION = "Proxy-Connection"
    static final String TRAILER = "Trailer"
    static final String TRANSFER_ENCODING = "Transfer-Encoding"
    static final String UPGRADE = "Upgrade"
    static final String VIA = "Via"
    static final String WARNING = "Warning"
    static final String NEGOTIATE = "Negotiate"

    /**
     * Entity Fields.
     */
    static final String ALLOW = "Allow"
    static final String CONTENT_ENCODING = "Content-Encoding"
    static final String CONTENT_LANGUAGE = "Content-Language"
    static final String CONTENT_LENGTH = "Content-Length"
    static final String CONTENT_LOCATION = "Content-Location"
    static final String CONTENT_MD5 = "Content-MD5"
    static final String CONTENT_RANGE = "Content-Range"
    static final String CONTENT_TYPE = "Content-Type"
    static final String EXPIRES = "Expires"
    static final String LAST_MODIFIED = "Last-Modified"

    /**
     * Request Fields.
     */
    static final String ACCEPT = "Accept"
    static final String ACCEPT_CHARSET = "Accept-Charset"
    static final String ACCEPT_ENCODING = "Accept-Encoding"
    static final String ACCEPT_LANGUAGE = "Accept-Language"
    static final String AUTHORIZATION = "Authorization"
    static final String EXPECT = "Expect"
    static final String FORWARDED = "Forwarded"
    static final String FROM = "From"
    static final String HOST = "Host"
    static final String IF_MATCH = "If-Match"
    static final String IF_MODIFIED_SINCE = "If-Modified-Since"
    static final String IF_NONE_MATCH = "If-None-Match"
    static final String IF_RANGE = "If-Range"
    static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since"
    static final String KEEP_ALIVE = "Keep-Alive"
    static final String MAX_FORWARDS = "Max-Forwards"
    static final String PROXY_AUTHORIZATION = "Proxy-Authorization"
    static final String RANGE = "Range"
    static final String REQUEST_RANGE = "Request-Range"
    static final String REFERER = "Referer"
    static final String TE = "TE"
    static final String USER_AGENT = "User-Agent"
    static final String X_FORWARDED_FOR = "X-Forwarded-For"
    static final String X_FORWARDED_PROTO = "X-Forwarded-Proto"
    static final String X_FORWARDED_SERVER = "X-Forwarded-Server"
    static final String X_FORWARDED_HOST = "X-Forwarded-Host"

    /**
     * Response Fields.
     */
    static final String ACCEPT_RANGES = "Accept-Ranges"
    static final String AGE = "Age"
    static final String ETAG = "ETag"
    static final String LOCATION = "Location"
    static final String PROXY_AUTHENTICATE = "Proxy-Authenticate"
    static final String RETRY_AFTER = "Retry-After"
    static final String SERVER = "Server"
    static final String SERVLET_ENGINE = "Servlet-Engine"
    static final String VARY = "Vary"
    static final String WWW_AUTHENTICATE = "WWW-Authenticate"

    /**
     * Other Fields.
     */
    static final String COOKIE = "Cookie"
    static final String SET_COOKIE = "Set-Cookie"
    static final String SET_COOKIE2 = "Set-Cookie2"
    static final String MIME_VERSION = "MIME-Version"
    static final String IDENTITY = "identity"

    static final String X_POWERED_BY = "X-Powered-By"

}
