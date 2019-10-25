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

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.WaitStrategy

// https://github.com/testcontainers/testcontainers-java/issues/318
abstract class KGenericContainer(imageName: String, version: String?) :
    GenericContainer<KGenericContainer>(imageName + (version?.let { ":$it" } ?: "")) {

    val rpcPort: Int

    init {
        withExposedPorts(8545)
        withCommand(*commands())
        waitingFor(isLive())
        withLogConsumer { println(it.utf8String) }
        start()

        this.rpcPort = getMappedPort(8545)

    }

    abstract fun commands(): Array<String>

    abstract fun isLive(): WaitStrategy
}
