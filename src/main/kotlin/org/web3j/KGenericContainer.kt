/*
 * Copyright 2019 web3j.
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
package org.web3j

import com.github.dockerjava.api.command.CreateContainerCmd
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.utility.MountableFile
import java.nio.file.Path
import java.util.function.Consumer

// https://github.com/testcontainers/testcontainers-java/issues/318
abstract class KGenericContainer(imageName: String, version: String?, private val genesisPath: Path) :
    GenericContainer<KGenericContainer>(imageName + (version?.let { ":$it" } ?: "")) {

    var rpcPort: Int = 0

    fun startNode() {
        withLogConsumer { println(it.utf8String) }
        withExposedPorts(8545)
        withCopyFileToContainer(MountableFile.forHostPath(genesisPath), "/genesis.json")
//        withCopyFileToContainer(MountableFile.forClasspathResource("geth_start.sh"), "/start.sh")
//        withCreateContainerCmdModifier { c -> c.withEntrypoint("/start.sh") }
        withCommand(*commands())
        waitingFor(withWaitStrategy())
        start()
        rpcPort = getMappedPort(8545)
    }

    abstract fun commands(): Array<String>

    protected abstract fun withWaitStrategy(): WaitStrategy
}
