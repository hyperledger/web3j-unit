package org.web3j.container.besu

import java.nio.file.Path
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.web3j.KGenericContainer
import org.web3j.abi.datatypes.Address

class BesuContainer(private val version: String?, private val genesisPath: Path?) :
    KGenericContainer("hyperledger/besu", version ) {

    constructor(version: String) : this(version, null)
    constructor(genesisPath: Path) : this(null, genesisPath)
    constructor() : this(null, null)

    override fun isLive(): WaitStrategy =
        Wait
            .forHttp("/liveness")
            .forStatusCode(200).forPort(8545)


    override fun commands(): Array<String> = arrayOf(
        "--rpc-http-enabled",
        "--miner-enabled",
        "--miner-coinbase=${Address.DEFAULT}",
        genesisPath?.let { "--genesis-file=$genesisPath" } ?: "--network=dev")
}
