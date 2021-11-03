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
package org.web3j

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.web3j.greeter.Greeter
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
@EVMTest(NodeType.OPEN_ETHEREUM)
class OpenethereumGreeterTest {

    @Test
    fun greeterDeploys(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {

        val bal =
            web3j.ethGetBalance("0x627306090abaB3A6e1400e9345bC60c78a8BEf57", DefaultBlockParameterName.LATEST).send()
        println(bal)

        val block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send()
        println("${block.block.number} SECOND BLOCK")

        val greeter = Greeter.deploy(web3j, transactionManager, gasProvider, "Hello EVM").send()
        val block2 = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send()
        println(block2.block.number)
        val greeting = greeter.greet().send()
        assertEquals("Hello EVM", greeting)
    }
}
