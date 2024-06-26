/*
 * Copyright 2019 Web3 Labs Ltd.
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
package org.web3j.container

import org.testcontainers.containers.DockerComposeContainer
import org.web3j.protocol.Web3jService
import org.web3j.protocol.http.HttpService
import java.io.File

open class KDockerComposeContainer(
    dockerComposePath: String,
    private val serviceName: String,
    private val containerPort: Int,
) :
    DockerComposeContainer<KDockerComposeContainer>(File(dockerComposePath)), GenericService {
    override fun startService(): Web3jService {
        withLogConsumer(serviceName) { print(it.utf8String) }
        withExposedService(serviceName, containerPort)
        start()
        val mappedPort = getServicePort(serviceName, containerPort)
        return HttpService("http://localhost:$mappedPort")
    }
}
