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

import org.junit.jupiter.api.extension.ExtendWith

/**
 * {@code @EVMTest} is a JUnit Jupiter extension to activate automatic
 * startup and stop of Ethereum Services used in a test case.
 *
 * <p><strong>Note:</strong> This extension has only be tested with sequential
 * test execution. Using it with parallel test execution is unsupported and
 * may have unintended side effects.</p>
 *
 * <p>Example:</p>
 *
 * <pre>
 * &#64;EVMTest
 * class MyContractTest {
 *
 *     &#64;Test
 *     void test(Web3j web3j, TransactionManager transactionManager, GasProvider gasProvider) {
 *         MyContract.deploy(web3j, transactionManager, gasProvider).send()
 *     }
 * }
 * </pre>
 *
 * @see Container
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(EVMExtension::class)
annotation class EVMTest(
    val type: NodeType = NodeType.EMBEDDED,
    val version: String = "latest",
    val genesis: String = "dev"
)
