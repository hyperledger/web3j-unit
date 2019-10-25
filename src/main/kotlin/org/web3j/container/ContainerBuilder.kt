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
package org.web3j.container

import java.lang.RuntimeException
import java.nio.file.Path
import org.web3j.KGenericContainer
import org.web3j.NodeType
import org.web3j.container.besu.BesuContainer
import org.web3j.container.geth.GethContainer

class ContainerBuilder {

    private var genesisPath: Path? = null
    private var type: NodeType = NodeType.BESU
    private var version: String? = null

    fun type(type: NodeType) = apply {
        this.type = type
    }

    fun version(version: String?) = apply {
        this.version = version
    }

    fun withGenesis(genesisPath: Path) = apply {
        this.genesisPath = genesisPath
    }

    fun build() = when (type) {
            NodeType.BESU -> BesuContainer(version, genesisPath)
            NodeType.GETH -> GethContainer(version, genesisPath)
//            NodeType.PARITY -> ParityContainer(genesisPath)
            else -> throw RuntimeException("Container Type Not Supported: $type")
        }
}
