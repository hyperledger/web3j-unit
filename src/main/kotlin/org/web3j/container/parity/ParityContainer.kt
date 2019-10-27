package org.web3j.container.parity

import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.web3j.KGenericContainer
import java.nio.file.Path

class ParityContainer(version: String?, genesisPath: Path) :
    KGenericContainer("parity/parity", version, genesisPath) {

    override fun withWaitStrategy(): WaitStrategy =
        Wait.forHttp("/").forStatusCode(200).forPort(8545)


    override fun commands(): Array<String> = arrayOf(
        "--config=dev",
        "--chain=/genesis.json")

}
