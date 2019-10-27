#!/bin/sh
geth --nousb init ./genesis.json
geth --nousb --rpc --rpcaddr=0.0.0.0 --mine --minerthreads=1 --miner.etherbase="0x0000000000000000000000000000000000000001"