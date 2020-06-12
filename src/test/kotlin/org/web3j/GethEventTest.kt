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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.humanstandardtoken.HumanStandardToken
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@EVMTest(NodeType.GETH)
class GethEventTest {

    @Test
    fun onContractEvent(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        val contract = HumanStandardToken.deploy(
            web3j, transactionManager, gasProvider,
            BigInteger.TEN, "Test", BigInteger.ZERO, "TEST"
        ).send()

        val countDownLatch = CountDownLatch(2)
        Thread {
            contract.approvalEventFlowable(EthFilter()).blockingFirst()
            contract.transferEventFlowable(EthFilter()).blockingFirst()
            countDownLatch.countDown()
            countDownLatch.countDown()
        }.start()

        contract.approve("fe3b557e8fb62b89f4916b721be55ceb828dbd73", BigInteger.TEN).send()
        contract.transfer("fe3b557e8fb62b89f4916b721be55ceb828dbd73", BigInteger.TEN).send()
        Assertions.assertTrue(countDownLatch.await(60, TimeUnit.SECONDS))
    }
}
