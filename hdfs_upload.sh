#!/bin/bash

URL=$1
FILENAME=$(basename "$URL")

docker exec namenode1 curl -L -o $FILENAME $URL
docker exec namenode1 hdfs dfs -mkdir -p hdfs://hdfs-cluster/data/taxi
docker exec namenode1 hdfs dfs -put $FILENAME hdfs://hdfs-cluster/data/taxi/$FILENAME
docker exec namenode1 rm $FILENAME
