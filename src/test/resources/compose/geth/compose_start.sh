#!/bin/sh
geth --nousb init /geth/test.json
geth --nousb account import /geth/key.txt --password /geth/password.txt
geth --nousb --rpc --rpcaddr=0.0.0.0 --allow-insecure-unlock --unlock 0xfe3b557e8fb62b89f4916b721be55ceb828dbd73 --password /geth/password.txt --mine
