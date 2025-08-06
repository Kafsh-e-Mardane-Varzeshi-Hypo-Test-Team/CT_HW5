#!/bin/bash

# Start SSH service
service ssh start

# Check the role of this container
if [ "$HADOOP_ROLE" = "journalnode" ]; then
    echo "Starting as JournalNode..."
    
    # Start journalnode
    echo "Starting JournalNode..."
    /hadoop/bin/hdfs --daemon start journalnode
    
elif [ "$HADOOP_ROLE" = "namenode1" ]; then
    echo "Starting as NameNode 1..."
    
    # Wait for journalnode and zookeeper to be ready
    echo "Waiting for JournalNode and ZooKeeper to be ready..."
    sleep 45
    
    # Format the NameNode (only run once)
    if [ ! -f /hadoop/dfs/name/current/VERSION ]; then
        echo "Formatting NameNode 1..."
        /hadoop/bin/hdfs namenode -format
    fi
    
    # Start namenode
    echo "Starting NameNode 1..."
    /hadoop/bin/hdfs --daemon start namenode
    
    # Wait for namenode to be ready
    echo "Waiting for NameNode 1 to be ready..."
    sleep 15
    
    # Initialize shared edits (only from nn1)
    echo "Initializing shared edits..."
    /hadoop/bin/hdfs namenode -initializeSharedEdits -force
    
    # Format ZKFC (only if not already formatted)
    echo "Checking if ZKFC needs formatting..."
    # Try to format ZKFC non-interactively, ignore errors if already formatted
    /hadoop/bin/hdfs zkfc -formatZK -nonInteractive || echo "ZKFC formatting skipped (already formatted or failed)"
    
    # Start ZKFC
    echo "Starting ZKFC..."
    /hadoop/bin/hdfs --daemon start zkfc
    
    # Start datanodes
    echo "Starting DataNodes..."
    /hadoop/bin/hdfs --daemon start datanode
    
elif [ "$HADOOP_ROLE" = "namenode2" ]; then
    echo "Starting as NameNode 2 (Standby)..."
    
    # Wait for journalnode, zookeeper and namenode1 to be ready
    echo "Waiting for JournalNode, ZooKeeper and NameNode 1 to be ready..."
    sleep 75
    
    # Bootstrap standby namenode (only run once)
    if [ ! -f /hadoop/dfs/name/current/VERSION ]; then
        echo "Bootstrapping Standby NameNode..."
        /hadoop/bin/hdfs namenode -bootstrapStandby
    fi
    
    # Start standby namenode
    echo "Starting Standby NameNode..."
    /hadoop/bin/hdfs --daemon start namenode
    
    # Start ZKFC
    echo "Starting ZKFC..."
    /hadoop/bin/hdfs --daemon start zkfc
    
    # Start datanodes
    echo "Starting DataNodes..."
    /hadoop/bin/hdfs --daemon start datanode
    
elif [ "$HADOOP_ROLE" = "datanode" ]; then
    echo "Starting as DataNode..."
    
    # Wait for namenodes to be ready
    echo "Waiting for NameNodes to be ready..."
    sleep 105
    
    # Start datanode
    echo "Starting DataNode..."
    /hadoop/bin/hdfs --daemon start datanode
fi

echo "HDFS HA Auto-Failover service started. Container will keep running..."
# Keep the container running
tail -f /dev/null 