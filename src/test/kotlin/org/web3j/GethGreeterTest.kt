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

@EVMTest(type = NodeType.GETH)
class GethGreeterTest {

    @Test
    fun greeterDeploys(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider,
    ) {
        Thread.sleep(10000)
        val balance =
            web3j.ethGetBalance("0xfe3b557e8fb62b89f4916b721be55ceb828dbd73", DefaultBlockParameterName.LATEST).send()
                .balance
        println(balance)
        val greeter = Greeter.deploy(web3j, transactionManager, gasProvider, "Hello EVM").send()
        val greeting = greeter.greet().send()
        assertEquals("Hello EVM", greeting)
    }
}
