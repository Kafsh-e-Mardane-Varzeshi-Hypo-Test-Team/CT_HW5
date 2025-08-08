#!/bin/bash

echo "Starting NameNode 1 (Primary) initialization..."

service ssh start

sleep 45

if [ ! -f /hadoop/dfs/name/current/VERSION ]; then
    echo "Formatting NameNode 1..."
    hdfs namenode -format
fi

echo "Starting NameNode 1..."
hdfs --daemon start namenode

sleep 15

echo "Initializing shared edits..."
hdfs namenode -initializeSharedEdits -force

echo "Checking if ZKFC needs formatting..."
hdfs zkfc -formatZK -nonInteractive

echo "Starting ZKFC..."
hdfs --daemon start zkfc

echo "NameNode 1 (Primary) started successfully."
tail -f /dev/null
