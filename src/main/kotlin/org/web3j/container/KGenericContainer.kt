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

import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait.forHttp
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.utility.MountableFile
import org.web3j.protocol.Web3jService
import org.web3j.protocol.http.HttpService
import java.time.Duration

// https://github.com/testcontainers/testcontainers-java/issues/318
open class KGenericContainer(
    imageName: String,
    version: String?,
    private val resourceFiles: HashMap<String, String>,
    private val hostFiles: HashMap<String, String>,
    private val startUpScript: String,
    private val genesis: String,
    private val rpcPort: Int,
) :
    GenericContainer<KGenericContainer>(imageName + (version?.let { ":$it" } ?: "")),
    GenericService {

    override fun startService(): Web3jService {
        resolveGenesis()
        withLogConsumer { print(it.utf8String) }
        addFixedExposedPort(8545, rpcPort)
        withCopyFileToContainer(MountableFile.forClasspathResource(startUpScript, 775), "/start.sh")
        resourceFiles.forEach { (source, target) ->
            withCopyFileToContainer(MountableFile.forClasspathResource(source), target)
        }
        hostFiles.forEach { (source, target) ->
            withClasspathResourceMapping(source, target, BindMode.READ_ONLY)
        }
        withCreateContainerCmdModifier { c -> c.withEntrypoint("/start.sh") }
        waitingFor(withWaitStrategy())
        start()

        return HttpService("http://localhost:${getMappedPort(8545)}")
    }

    open fun resolveGenesis() {
        genesis.let {
            val resolvedGenesis = if (it.endsWith(".json")) it else "$it.json"
            if (inClassPath(resolvedGenesis)) {
                resourceFiles[resolvedGenesis] = "/genesis.json"
            } else {
                hostFiles[resolvedGenesis] = "/genesis.json"
            }
        }
    }

    private fun inClassPath(path: String) = this.javaClass.classLoader.getResource(path) != null

    protected open fun withWaitStrategy(): WaitStrategy =
        forHttp("/").forStatusCode(200).forPort(8545).withStartupTimeout(Duration.ofMinutes(2))
}
