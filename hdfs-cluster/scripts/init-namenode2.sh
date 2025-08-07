#!/bin/bash

echo "Starting NameNode 2 (Standby) initialization..."

service ssh start

echo "Waiting for JournalNode, ZooKeeper and NameNode 1 to be ready..."
sleep 75

if [ ! -f /hadoop/dfs/name/current/VERSION ]; then
    echo "Bootstrapping Standby NameNode..."
    hdfs namenode -bootstrapStandby
fi

echo "Starting Standby NameNode..."
hdfs --daemon start namenode

echo "Starting ZKFC..."
hdfs --daemon start zkfc

echo "NameNode 2 (Standby) started successfully."
tail -f /dev/null
