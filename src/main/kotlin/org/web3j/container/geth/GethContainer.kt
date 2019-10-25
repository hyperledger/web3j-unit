package org.web3j.container.geth

import java.nio.file.Path
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.web3j.KGenericContainer
import org.web3j.abi.datatypes.Address
import java.time.Duration

class GethContainer(private val version: String?, private val genesisPath: Path?) :
    KGenericContainer("ethereum/client-go", version) {

    constructor(version: String) : this(version, null)
    constructor(genesisPath: Path) : this(null, genesisPath)
    constructor() : this(null, null)

    override fun isLive(): WaitStrategy =
        Wait.forHttp("/").forStatusCode(200).forPort(8545)


    override fun commands(): Array<String> = arrayOf(
        "--rpc",
        "--rpcaddr=0.0.0.0",
        "--mine",
        "--miner.etherbase=${Address.DEFAULT}",
        genesisPath?.let { " init $genesisPath" } ?: "--dev")
}
