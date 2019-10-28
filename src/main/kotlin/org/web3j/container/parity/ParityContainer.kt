package org.web3j.container.parity

import org.web3j.container.KGenericContainer

class ParityContainer(
    version: String?,
    resourceFiles: HashMap<String, String>,
    hostFiles: HashMap<String, String>,
    genesisPath: String
) :
    KGenericContainer(
        "parity/parity",
        version,
        resourceFiles,
        hostFiles,
        "parity/parity_start.sh",
        if (genesisPath == "dev") "parity/$genesisPath" else genesisPath)
