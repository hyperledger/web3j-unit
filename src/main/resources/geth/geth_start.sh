#!/bin/sh
geth --nousb init ./genesis.json
geth --nousb account import /key.txt --password /password.txt
geth --nousb --rpc --http.api "eth,net,web,txpool" --rpcaddr=0.0.0.0 --allow-insecure-unlock --unlock 0xfe3b557e8fb62b89f4916b721be55ceb828dbd73 --password /password.txt --mine
