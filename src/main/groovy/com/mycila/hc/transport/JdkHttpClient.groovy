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
package com.mycila.hc.transport

import com.mycila.hc.*
import com.mycila.hc.io.ContentDecoder
import com.mycila.hc.io.DeferredContentProvider
import com.mycila.hc.io.GZIPContentDecoder
import com.mycila.hc.io.NoContentDecoder

import javax.net.ssl.*
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-12
 */
class JdkHttpClient extends HttpClient {

    @Override
    protected HttpResult send(HttpExchange exchange) throws Exception {

        DeferredContentProvider deferredContentProvider = null
        HttpRequest request = exchange.request
        HttpResponse response = exchange.response
        HttpURLConnection connection = ((HttpURLConnection) request.requestUrl.openConnection(config.proxy))

        try {
            connection.with {
                doInput = true
                doOutput = request.hasContent()
                requestMethod = request.method
                useCaches = config.allowCache
                instanceFollowRedirects = config.followRedirects
                connectTimeout = config.connectTimeout
                readTimeout = config.readTimeout
                long contentLength = request.headers.contentLength
                if (contentLength >= 0) {
                    fixedLengthStreamingMode = contentLength
                }
                if (request.headers.chunked) {
                    chunkedStreamingMode = config.maxBufferSize
                }
                for (Map.Entry<String, String> header : request.headers.stringIterator(',')) {
                    setRequestProperty(header.key, header.value)
                }
            }

            // ssl verifier
            if (request.secured && !config.sslVerification) {
                HttpsURLConnection httpsCon = (HttpsURLConnection) connection
                httpsCon.SSLSocketFactory = NoSSL.INSTANCE.factory
                httpsCon.hostnameVerifier = NoSSL.INSTANCE.hostVerifier
            }

            Log.trace('[%s] JdkHttpClient Connecting...', null, request.url)

            boolean done = false
            try {
                connection.connect()
            } catch (IOException ignored) {
                // in case of status >= 400
                done = true
            }

            // write request content
            if (!done && request.hasContent()) {

                Log.trace('[%s] JdkHttpClient Sending request body...', null, request.url)

                try {
                    OutputStream out = connection.outputStream
                    byte[] bytes = new byte[config.maxBufferSize]
                    for (ByteBuffer buffer : request.contentProvider) {
                        while (buffer.hasRemaining()) {

                            // gives a chance to abort request
                            if (Thread.currentThread().interrupted) {
                                throw new InterruptedException()
                            }

                            int len = Math.min(bytes.length, buffer.remaining())
                            buffer.get(bytes, 0, len)
                            out.write(bytes, 0, len)
                        }
                    }
                    out.close()
                } finally {
                    request.contentProvider.close()
                }
            }

            // get response code and headers
            response.status = connection.responseCode
            response.reason = connection.responseMessage
            connection.headerFields.each { k, list ->
                if (k != null) {
                    response.headers.add(k, list)
                }
            }

            Log.trace('[%s] JdkHttpClient onHeaders: %s %s', null, request.url, response.status, response.reason)

            request.listeners.onHeaders(exchange)

            // gives a chance to abort request
            if (Thread.currentThread().interrupted) {
                throw new InterruptedException()
            }

            // do not read response if not needed
            if (request.responseContentHandling == HttpClientConfig.ResponseContentHandling.IGNORED) {
                connection.disconnect()
                return new HttpResult(request, response)
            }

            // otherwise try to get fast an input stream
            InputStream stream = connection.errorStream
            if (stream == null) {
                stream = connection.inputStream
            }

            boolean gzip = response.headers.getFirst(HttpHeader.CONTENT_ENCODING) == 'gzip'
            boolean buffered = request.responseContentHandling == HttpClientConfig.ResponseContentHandling.BUFFERED

            Log.trace('[%s] JdkHttpClient Reading response content (gzip=%s, buffered=%s)...', null, request.url, gzip, buffered)

            // gzip decoding ?
            ContentDecoder decoder = gzip ? new GZIPContentDecoder(config.maxBufferSize) : new NoContentDecoder()

            // create a byte-capturing content provider if buffered
            if (buffered) {
                deferredContentProvider = new DeferredContentProvider(connection.contentLengthLong, true)
                request.listeners.addFirst(deferredContentProvider)
            }

            // reader task
            int read = 0
            while (read != -1 && !Thread.currentThread().interrupted) {
                byte[] bytes = new byte[config.maxBufferSize]
                int offset = 0
                read = stream.read(bytes, 0, bytes.length)
                while (read > 0 && offset + read <= bytes.length && !Thread.currentThread().interrupted) {
                    offset += read
                    read = stream.read(bytes, offset, bytes.length - offset)
                }
                if (Thread.currentThread().interrupted) {
                    throw new InterruptedException()
                }
                if (offset > 0) {
                    ByteBuffer data = decoder.decode(ByteBuffer.wrap(bytes, 0, offset))
                    if (data.hasRemaining()) {
                        Log.trace('[%s] JdkHttpClient onContent: %s bytes', null, request.url, data.remaining())
                        exchange.request.listeners.onContent(new HttpContent(exchange, data))
                    }
                    // gives a chance to abort request
                    if (Thread.currentThread().interrupted) {
                        throw new InterruptedException()
                    }
                }
            }

            return deferredContentProvider ?
                new HttpResult(request, response, config.deserializers, deferredContentProvider) :
                new HttpResult(request, response)

        } catch (InterruptedException e) {
            Log.trace('[%s] JdkHttpClient interrupted', null, request.url)
            Thread.currentThread().interrupt()
            throw e
        } catch (Exception e) {
            Log.trace('[%s] JdkHttpClient error: %s', e, request.url, e.message)
            throw e
        } finally {
            Log.trace('[%s] JdkHttpClient cleanup', null, request.url)
            deferredContentProvider?.close()
            request.listeners.remove(deferredContentProvider)
            connection.disconnect()
        }
    }

    static class NoSSL {

        static NoSSL INSTANCE = new NoSSL()

        SSLSocketFactory factory

        HostnameVerifier hostVerifier = new HostnameVerifier() {
            @Override
            boolean verify(String s, SSLSession sslSession) {
                return true
            }
        }

        NoSSL() {
            X509TrustManager trustAllCerts = new X509TrustManager() {
                @Override
                void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                }

                @Override
                void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                }

                @Override
                X509Certificate[] getAcceptedIssuers() {
                    return null
                }
            }
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, [trustAllCerts] as TrustManager[], new SecureRandom());
            factory = sslContext.getSocketFactory()
        }
    }

}
