# HDFS Cluster with Docker Compose

A high-availability HDFS cluster managed with Docker Compose.  The project provides a local distributed data-processing environment with HDFS storage and Apache Spark for executing data-processing jobs.

## Architecture
The cluster includes two NameNodes, two DataNodes, one JournalNode, and one ZooKeeper instance for failover management and active NameNode election. 

| Component | Role | Port |
|---|---|---|
| ZooKeeper | Failover management and NameNode election | 2181 |
| JournalNode | Stores HDFS metadata changes | 8485 |
| NameNode1 / NameNode2 | One active and one standby NameNode | 9870 / 9871 |
| DataNode1 / DataNode2 | HDFS data storage | — |

## Prerequisites

- Docker
- Docker Compose

## Start the Cluster

```bash
docker compose up -d
```

Check container status:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f
```

## Access the HDFS Web UI

- NameNode1 Active: http://localhost:9870
- NameNode2 Standby: http://localhost:9871

## Work with HDFS

Enter the NameNode container:

```bash
docker exec -it namenode1 bash
```

Common HDFS commands:

```bash
hdfs dfs -mkdir -p hdfs://hdfs-cluster/data
hdfs dfs -put /path/to/local/file hdfs://hdfs-cluster/data/input.txt
hdfs dfs -ls hdfs://hdfs-cluster/data
hdfs dfs -cat hdfs://hdfs-cluster/data/input.txt
```

Upload datasets with the helper script from the project root:

```bash
./hdfs_upload.sh <source-url>
```

Examples:

```bash
./hdfs_upload.sh https://d37ci6vzurychx.cloudfront.net/misc/taxi_zone_lookup.csv
./hdfs_upload.sh https://d37ci6vzurychx.cloudfront.net/trip-data/yellow_tripdata_2025-02.parquet
```

## Build Spark Jobs

Build the JAR files and place them in the Spark client directory:

```bash
cd spark-jobs
mvn clean package
cp q*/target/*.jar ../spark-client/jars
```

## Run a Spark Job

```bash
docker exec -it spark-client ./bin/spark-submit /job-jars/q1-0.1.jar
```

Replace `q1` with the desired job, for example `q2`, `q3`, or `q4`.

## Check Output

View output in the HDFS Web UI at http://localhost:9870 under `/output`, or run:

```bash
docker exec namenode1 hdfs dfs -ls /output
```

## Project Structure

```text
.
├── docker-compose.yaml
├── hdfs_upload.sh
├── hdfs-cluster/
│   ├── config/
│   ├── scripts/
│   └── Dockerfile
├── spark-client/
│   ├── config/
│   └── Dockerfile
├── spark-jobs/
│   ├── pom.xml
│   ├── q1/
│   ├── q2/
│   ├── q3/
│   └── q4/
└── spark-outputs/
```

## Cleanup

Stop the cluster:

```bash
docker compose down
```

Stop the cluster and remove all volumes, including HDFS data:

```bash
docker compose down -v
```

## Notes

- Data is persisted in Docker volumes.
- Use `docker compose down -v` only when you want to delete all cluster data.
- Use `docker compose` commands for Docker Compose V2.
