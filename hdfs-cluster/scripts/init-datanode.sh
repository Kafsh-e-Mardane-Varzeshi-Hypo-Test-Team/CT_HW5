#!/bin/bash

echo "Starting DataNode initialization..."

service ssh start

echo "Waiting for NameNodes to be ready..."
sleep 105

echo "Starting DataNode..."
hdfs --daemon start datanode

echo "DataNode started successfully."
tail -f /dev/null
