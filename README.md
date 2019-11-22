# Web3j-unit [![Build Status](https://travis-ci.org/web3j/web3j-unit.svg?branch=master)](https://travis-ci.org/web3j/web3j-unit)

**⚠️ This is a work in progress! ⚠**

Web3j-unit is a [Junit 5](https://junit.org/junit5/docs/current/user-guide/) extension to streamline the creation of Ethereum contract tests.

Multiple Ethereum implementations are supported including Geth and Besu. To run tests built using Web3j-unit, **docker is required** on the host.

Instances of `Web3j`, `TransactionManager` and `GasProvider` are injected into the Junit runner.

### Getting Started

1. Add dependency to gradle. **N.B.** Only snapshots are available at this time.

```groovy
   repositories {
       maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
       maven { url "https://dl.bintray.com/ethereum/maven/" }
   }

   implementation "org.web3j:core:4.6.0-SNAPSHOT"
   testCompile "org.web3j:web3j-unit:4.6.0-SNAPSHOT"
```

2. Create a new test with the `@EVMTest` annotation. An instance of Besu is used by default. To use Geth pass the node type into the annotation: `@EVMTest(NodeType.GETH)`

```kotlin
@EVMTest
class GreeterTest {

}
```

3. Inject instance of `Web3j` `TransactionManager` and `ContractGasProvider` in your test method.

```kotlin
@EVMTest
class GreeterTest {

@Test
    fun greeterDeploys(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {}
}
```

4. Deploy your contract in the test.

```kotlin
@EVMTest
class GreeterTest {

    @Test
    fun greeterDeploys(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        val greeter = Greeter.deploy(web3j, transactionManager, gasProvider, "Hello EVM").send()
        val greeting = greeter.greet().send()
        assertEquals("Hello EVM", greeting)
    }
}
```

5. Run the test!
