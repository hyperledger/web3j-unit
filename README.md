# Web3j-unit [![Build Status](https://travis-ci.org/web3j/web3j-unit.svg?branch=master)](https://travis-ci.org/web3j/web3j-unit)

Web3j-unit is a [Junit 5](https://junit.org/junit5/docs/current/user-guide/) extension to streamline the creation of Ethereum contract tests.

Multiple Ethereum implementations are supported including Geth and Besu. To run tests built using Web3j-unit, **docker is required** on the host.

Instances of `Web3j`, `TransactionManager` and `GasProvider` are injected into the Junit runner.

You can find a sample [here](https://github.com/cfelde/web3j-unitexample).

### Getting Started

1. Add dependency to gradle.

```groovy
   repositories {
      mavenCentral()
   }

   implementation "org.web3j:core:4.5.9"
   testCompile "org.web3j:web3j-unit:4.5.9"
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

### Using a custom docker-compose file

1. Add dependency to gradle.
   
```groovy
  repositories {
     mavenCentral()
  }

  implementation "org.web3j:core:4.5.9"
  testCompile "org.web3j:web3j-unit:4.5.9"
```

2. Create a new test with the `@EVMComposeTest` annotation.
By default, uses `test.yml` file in the project home, and runs `web3j` on service name `node1` exposing the port `8545`. 
Can be customised to use specific docker-compose file, service name and port by `@EVMComposeTest("src/test/resources/geth.yml", "ethnode1", 8080)`
Here, we connect to the service named `ethnode1` in the `src/test/resources/geth.yml` docker-compose file which exposes the port `8080` for `web3j` to connect to. 

```kotlin
@EVMComposeTest("src/test/resources/geth.yml", "ethnode1", 8080)
class GreeterTest {

}
```

3. Inject instance of `Web3j` `TransactionManager` and `ContractGasProvider` in your test method.

```kotlin
@EVMComposeTest("src/test/resources/geth.yml", "ethnode1", 8080)
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
@EVMComposeTest("src/test/resources/geth.yml", "ethnode1", 8080)
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