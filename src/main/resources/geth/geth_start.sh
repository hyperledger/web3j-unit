#!/bin/sh
geth --nousb init ./genesis.json
geth --nousb account import /key.txt --password /password.txt
geth --nousb --rpc --rpcaddr=0.0.0.0 --unlock 0xfe3b557e8fb62b89f4916b721be55ceb828dbd73 --password /password.txt --mine
