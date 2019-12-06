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

import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ExtensionConfigurationException
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.AnnotationUtils
import org.web3j.container.ServiceBuilder
import org.web3j.protocol.Web3j
import org.web3j.tx.FastRawTransactionManager
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.utils.Async
import java.util.Optional

class EVMComposeExtension : EVMExtension() {

    override fun beforeAll(context: ExtensionContext) {
        val evmCustomTest = AnnotationUtils
            .findAnnotation(context.requiredTestClass, EVMComposeTest::class.java).orElseThrow()

        super.service = ServiceBuilder()
            .type(NodeType.COMPOSE)
            .withDockerCompose(evmCustomTest.dockerCompose)
            .withServiceName(evmCustomTest.service)
            .withServicePort(evmCustomTest.servicePort)
            .withSelfAddress(super.credentials.address)
            .build()

        super.web3j = Web3j.build(super.service.startService(), 500, Async.defaultExecutorService())

        super.transactionManager = FastRawTransactionManager(
            super.web3j,
            super.credentials,
            PollingTransactionReceiptProcessor(
                super.web3j,
                1000,
                30))
    }

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        return findEvmTests(context)
            .map { ConditionEvaluationResult.enabled("EVMCustomTest enabled") }
            .orElseThrow { ExtensionConfigurationException("@EVMCustomTest not found") }
    }
    private fun findEvmTests(context: ExtensionContext): Optional<EVMComposeTest> {
        var current = Optional.of(context)
        while (current.isPresent) {
            val evmTest = AnnotationUtils
                .findAnnotation(current.get().requiredTestClass, EVMComposeTest::class.java)
            if (evmTest.isPresent) {
                return evmTest
            }
            current = current.get().parent
        }
        return Optional.empty()
    }
}
