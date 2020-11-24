/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.strategy

import org.rnorth.ducttape.TimeoutException
import org.rnorth.ducttape.unreliables.Unreliables
import org.testcontainers.containers.ContainerLaunchException
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.shaded.com.google.common.base.Strings
import org.testcontainers.shaded.com.google.common.io.BaseEncoding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import kotlin.collections.HashSet

class CustomWaitingStrategy : AbstractWaitStrategy() {


    /**
     * Authorization HTTP header.
     */
    private val HEADER_AUTHORIZATION = "Authorization"

    /**
     * Basic Authorization scheme prefix.
     */
    private val AUTH_BASIC = "Basic "

    private var path = "/"
    private val statusCodes: MutableSet<Int> = HashSet()
    private var tlsEnabled = false
    private var username: String? = null
    private var password: String? = null
    private var responsePredicate: Predicate<String>? = null
    private var statusCodePredicate: Predicate<Int>? = null
    private var livenessPort = Optional.empty<Int>()
    private var readTimeout = Duration.ofSeconds(1)

    /**
     * Waits for the given status code.
     *
     * @param statusCode the expected status code
     * @return this
     */
    fun forStatusCode(statusCode: Int): CustomWaitingStrategy {
        statusCodes.add(statusCode)
        return this
    }

    /**
     * Waits for the status code to pass the given predicate
     * @param statusCodePredicate The predicate to test the response against
     * @return this
     */
    fun forStatusCodeMatching(statusCodePredicate: Predicate<Int>?): CustomWaitingStrategy {
        this.statusCodePredicate = statusCodePredicate
        return this
    }

    /**
     * Waits for the given path.
     *
     * @param path the path to check
     * @return this
     */
    fun forPath(path: String): CustomWaitingStrategy {
        this.path = path
        return this
    }

    /**
     * Wait for the given port.
     *
     * @param port the given port
     * @return this
     */
    fun forPort(port: Int): CustomWaitingStrategy {
        livenessPort = Optional.of(port)
        return this
    }

    /**
     * Indicates that the status check should use HTTPS.
     *
     * @return this
     */
    fun usingTls(): CustomWaitingStrategy {
        tlsEnabled = true
        return this
    }

    /**
     * Authenticate with HTTP Basic Authorization credentials.
     *
     * @param username the username
     * @param password the password
     * @return this
     */
    fun withBasicCredentials(username: String?, password: String?): CustomWaitingStrategy {
        this.username = username
        this.password = password
        return this
    }

    /**
     * Set the HTTP connections read timeout.
     *
     * @param timeout the timeout (minimum 1 millisecond)
     * @return this
     */
    fun withReadTimeout(timeout: Duration): CustomWaitingStrategy {
        require(timeout.toMillis() >= 1) { "you cannot specify a value smaller than 1 ms" }
        readTimeout = timeout
        return this
    }

    /**
     * Waits for the response to pass the given predicate
     * @param responsePredicate The predicate to test the response against
     * @return this
     */
    fun forResponsePredicate(responsePredicate: Predicate<String>?): CustomWaitingStrategy {
        this.responsePredicate = responsePredicate
        return this
    }

    override fun waitUntilReady() {
        val containerName = waitStrategyTarget.containerInfo.name
        val livenessCheckPort = livenessPort.map { originalPort: Int? ->
            waitStrategyTarget.getMappedPort(
                originalPort!!
            )
        }.orElseGet {
            val livenessCheckPorts = livenessCheckPorts
            if (livenessCheckPorts == null || livenessCheckPorts.isEmpty()) {
                //log.warn("{}: No exposed ports or mapped ports - cannot wait for status", containerName)
                return@orElseGet -1
            }
            livenessCheckPorts.iterator().next()
        }
        if (null == livenessCheckPort || -1 == livenessCheckPort) {
            return
        }
        val uri = buildLivenessUri(livenessCheckPort).toString()
        //log.info("{}: Waiting for {} seconds for URL: {}", containerName, startupTimeout.seconds, uri)

        // try to connect to the URL
        try {
            Unreliables.retryUntilSuccess(
                startupTimeout.seconds.toInt(), TimeUnit.SECONDS
            ) {
                rateLimiter.doWhenReady {
                    try {
                        val connection =
                            URL(uri).openConnection() as HttpURLConnection
                        connection.readTimeout = Math.toIntExact(readTimeout.toMillis())

                        // authenticate
                        if (!Strings.isNullOrEmpty(username)) {
                            connection.setRequestProperty(HEADER_AUTHORIZATION, buildAuthString(username, password))
                            connection.useCaches = false
                        }
                        connection.requestMethod = "OPTIONS"
                        connection.connect()
                    //    log.trace("Get response code {}", connection.responseCode)

                        // Choose the statusCodePredicate strategy depending on what we defined.
                        val predicate: Predicate<Int>?
                        predicate = if (statusCodes.isEmpty() && statusCodePredicate == null) {
                            // We have no status code and no predicate so we expect a 200 OK response code
                            Predicate { responseCode: Int -> HttpURLConnection.HTTP_OK == responseCode }
                        } else if (!statusCodes.isEmpty() && statusCodePredicate == null) {
                            // We use the default status predicate checker when we only have status codes
                            Predicate<Int> { responseCode: Int? ->
                                statusCodes.contains(
                                    responseCode
                                )
                            }
                        } else if (statusCodes.isEmpty()) {
                            // We only have a predicate
                            statusCodePredicate
                        } else {
                            // We have both predicate and status code
                            statusCodePredicate!!.or { responseCode: Int? ->
                                statusCodes.contains(
                                    responseCode
                                )
                            }
                        }
                        if (!predicate!!.test(connection.responseCode)) {
                            throw RuntimeException(
                                String.format(
                                    "HTTP response code was: %s",
                                    connection.responseCode
                                )
                            )
                        }
                        if (responsePredicate != null) {
                            val responseBody = getResponseBody(connection)
                            //log.trace("Get response {}", responseBody)
                            if (!responsePredicate!!.test(responseBody)) {
                                throw RuntimeException(
                                    String.format(
                                        "Response: %s did not match predicate",
                                        responseBody
                                    )
                                )
                            }
                        }
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
                true
            }
        } catch (e: TimeoutException) {
            throw ContainerLaunchException(
                String.format(
                    "Timed out waiting for URL to be accessible (%s should return HTTP %s)",
                    uri,
                    if (statusCodes.isEmpty()) HttpURLConnection.HTTP_OK else statusCodes
                )
            )
        }
    }

    /**
     * Build the URI on which to check if the container is ready.
     *
     * @param livenessCheckPort the liveness port
     * @return the liveness URI
     */
    private fun buildLivenessUri(livenessCheckPort: Int): URI {
        val scheme = (if (tlsEnabled) "https" else "http") + "://"
        val host = waitStrategyTarget.host
        val portSuffix: String
        portSuffix = if (tlsEnabled && 443 == livenessCheckPort || !tlsEnabled && 80 == livenessCheckPort) {
            ""
        } else {
            ":$livenessCheckPort"
        }
        return URI.create(scheme + host + portSuffix + path)
    }

    /**
     * @param username the username
     * @param password the password
     * @return a basic authentication string for the given credentials
     */
    private fun buildAuthString(username: String?, password: String?): String? {
        return AUTH_BASIC + BaseEncoding.base64().encode("$username:$password".toByteArray())
    }

    @Throws(IOException::class)
    private fun getResponseBody(connection: HttpURLConnection): String {
        val reader: BufferedReader
        reader = if (200 <= connection.responseCode && connection.responseCode <= 299) {
            BufferedReader(InputStreamReader(connection.inputStream))
        } else {
            BufferedReader(InputStreamReader(connection.errorStream))
        }
        val builder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            builder.append(line)
        }
        return builder.toString()
    }


}
