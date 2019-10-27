package org.web3j.container.geth

import java.nio.file.Path
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.testcontainers.utility.MountableFile
import org.web3j.KGenericContainer
import org.web3j.abi.datatypes.Address

class GethContainer(version: String?, genesisPath: Path) :
    KGenericContainer("ethereum/client-go", version, genesisPath) {

    override fun withWaitStrategy(): WaitStrategy =        Wait.forHttp("/").forStatusCode(200).forPort(8545)
//        Wait.forLogMessage(".*Successfully wrote genesis state.*", 1)


    override fun commands(): Array<String> = arrayOf(
        "--nousb",
        "init",
        "./genesis.json",
        "&&",
        "geth",
        "--nousb",
        "--rpc",
        "--rpcaddr=0.0.0.0",
        "--mine",
        "--minerthreads=1",
        "--miner.etherbase=0x0000000000000000000000000000000000000001")
}
