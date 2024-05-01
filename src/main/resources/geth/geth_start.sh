#!/bin/sh
geth init ./genesis.json
geth account import --password password.txt key.txt
geth --http --http.api "eth,net,txpool" --http.addr=0.0.0.0 --http.port 8545 --http.corsdomain '*' --allow-insecure-unlock --unlock 0xfe3b557e8fb62b89f4916b721be55ceb828dbd73 --password /password.txt --mine --miner.etherbase 0xfe3b557e8fb62b89f4916b721be55ceb828dbd73 --rpc.allow-unprotected-txs
