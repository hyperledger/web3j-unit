package org.web3j.container.geth

import org.web3j.container.KGenericContainer

class GethContainer(
    version: String?,
    resourceFiles: HashMap<String, String>,
    hostFiles: HashMap<String, String>,
    genesisPath: String
) :
    KGenericContainer(
        "ethereum/client-go",
        version,
        addKey(resourceFiles),
        hostFiles,
        "geth/geth_start.sh",
        if (genesisPath == "dev") "geth/$genesisPath" else genesisPath)

fun addKey(resourceFiles: java.util.HashMap<String, String>): java.util.HashMap<String, String> {
    return resourceFiles.let {
        it["geth/key.txt"] = "/key.txt"
        it["geth/password.txt"] = "/password.txt"
        it
    }
}
