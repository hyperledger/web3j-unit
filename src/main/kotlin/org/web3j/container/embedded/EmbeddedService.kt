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
package org.web3j.container.embedded

import org.hyperledger.besu.evm.tracing.OperationTracer
import org.web3j.container.GenericService
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedWeb3jService
import org.web3j.protocol.Web3jService

class EmbeddedService(val configuration: Configuration, val operationTracer: OperationTracer) : GenericService {
    override fun startService(): Web3jService {
        return EmbeddedWeb3jService(configuration, operationTracer)
    }

    override fun close() {
        // Nothing to close
    }
}
