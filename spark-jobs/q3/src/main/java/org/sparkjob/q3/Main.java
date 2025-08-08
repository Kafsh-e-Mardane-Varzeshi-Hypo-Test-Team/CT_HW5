package org.sparkjob.q3;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Main {
    private static final String INPUT_PATH = "hdfs://hdfs-cluster/data/taxi/";
    private static final String OUTPUT_PATH = "hdfs://hdfs-cluster/output/";
    private static final String TAXI_ZONE_LOOKUP_PATH = INPUT_PATH + "taxi_zone_lookup.csv";
    private static final String YELLOW_TRIP_DATA_PATH = INPUT_PATH + "yellow_tripdata_2025-02.parquet";

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("HDFS Test")
                .getOrCreate();

        Dataset<Row> df = spark.read().parquet(YELLOW_TRIP_DATA_PATH);
        df.show();
        df.repartition(1)
                .write()
                .mode("overwrite")
                .parquet(OUTPUT_PATH + "test");

    }
}
