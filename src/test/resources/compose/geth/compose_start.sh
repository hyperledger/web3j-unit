#!/bin/sh
geth init /geth/genesis.json
geth account import --password /geth/password.txt /geth/key.txt
geth --http --http.api "eth,net,txpool" --http.addr=0.0.0.0 --allow-insecure-unlock --unlock 0xfe3b557e8fb62b89f4916b721be55ceb828dbd73 --password /geth/password.txt --mine --miner.etherbase 0xfe3b557e8fb62b89f4916b721be55ceb828dbd73 --rpc.allow-unprotected-txs
