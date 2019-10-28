package org.web3j.container.besu

import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.WaitStrategy
import org.web3j.container.KGenericContainer

class BesuContainer(
    version: String?,
    resourceFiles: HashMap<String, String>,
    hostFiles: HashMap<String, String>,
    genesisPath: String
) :
    KGenericContainer(
        "hyperledger/besu",
        version,
        resourceFiles,
        hostFiles,
        "besu/besu_start.sh",
        if (genesisPath == "dev") "besu/$genesisPath" else genesisPath) {

    override fun withWaitStrategy(): WaitStrategy =
        Wait
            .forHttp("/liveness")
            .forStatusCode(200).forPort(8545)
}
