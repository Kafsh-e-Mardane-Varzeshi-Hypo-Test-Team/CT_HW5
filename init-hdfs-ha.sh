#!/bin/bash

# Format the NameNode (only on namenode1)
/hadoop/bin/hdfs namenode -format mycluster

# Start journalnodes and format ZKFC
/hadoop/sbin/start-dfs.sh

# Initialize shared edits (only from nn1)
/hadoop/bin/hdfs namenode -initializeSharedEdits -force

# Format ZKFC
/hadoop/bin/hdfs zkfc -formatZK

# Start the rest
/hadoop/sbin/hadoop-daemon.sh start zkfc
/hadoop/bin/hdfs --daemon start namenode