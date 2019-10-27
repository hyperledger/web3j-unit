package org.web3j.container.besu

import com.github.dockerjava.api.command.CreateContainerCmd
import java.nio.file.Path
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.utility.MountableFile
import org.web3j.KGenericContainer
import org.web3j.abi.datatypes.Address
import java.util.function.Consumer

class BesuContainer(version: String?, genesisPath: Path) :
    KGenericContainer("hyperledger/besu", version, genesisPath) {

    override fun withWaitStrategy(): WaitStrategy =
        Wait
            .forHttp("/liveness")
            .forStatusCode(200).forPort(8545)

    override fun commands(): Array<String> = arrayOf(
        "--rpc-http-enabled",
        "--miner-enabled",
        "--miner-coinbase=${Address.DEFAULT}",
        "--genesis-file=/genesis.json" )
}
