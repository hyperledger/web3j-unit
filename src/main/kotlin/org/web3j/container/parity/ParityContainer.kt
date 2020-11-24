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
package org.web3j.container.parity

import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.web3j.container.KGenericContainer
import org.web3j.strategy.CustomWaitingStrategy

class ParityContainer(
    version: String?,
    resourceFiles: HashMap<String, String>,
    hostFiles: HashMap<String, String>,
    genesisPath: String,
    rpcPort: Int
) :
    KGenericContainer(
        "openethereum/openethereum",
        version,
        resourceFiles,
        addHostFiles(hostFiles),
        "openethereum/openethereum_start.sh",
        if (genesisPath == "dev") "openethereum/$genesisPath" else genesisPath,
        rpcPort
    ) {
    override fun withWaitStrategy(): WaitStrategy {
        return CustomWaitingStrategy().forPort(8545).forPath("/").forStatusCode(200)
    }
}

fun addHostFiles(resourceFiles: java.util.HashMap<String, String>): java.util.HashMap<String, String> {
    return resourceFiles.let {
        it["openethereum/key.txt"] = "/etc/key.txt"
        it["openethereum/password.txt"] = "etc/password.txt"
        it["openethereum/config.toml"] = "/config.toml"
        it["openethereum/dev.json"] = "/etc/dev.json"
        it
    }
}
