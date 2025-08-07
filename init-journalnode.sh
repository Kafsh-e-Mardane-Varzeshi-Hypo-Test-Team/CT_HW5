#!/bin/bash

echo "Starting JournalNode initialization..."

service ssh start

echo "Starting JournalNode..."
hdfs --daemon start journalnode

echo "JournalNode started successfully."
tail -f /dev/null
