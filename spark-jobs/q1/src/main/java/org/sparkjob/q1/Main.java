package org.sparkjob.q1;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.*;

public class Main {
    private static final String INPUT_PATH = "hdfs://hdfs-cluster/data/taxi/";
    private static final String OUTPUT_PATH = "hdfs://hdfs-cluster/output/";
    private static final String TAXI_ZONE_LOOKUP_PATH = INPUT_PATH + "taxi_zone_lookup.csv";
    private static final String YELLOW_TRIP_DATA_PATH = INPUT_PATH + "yellow_tripdata_2025-02.parquet";

    public static void main(String[] args) {
        // Create Spark session
        SparkSession spark = SparkSession.builder()
                .appName("Analysis 1 - Long Trips with Multiple Passengers")
                .config("spark.sql.adaptive.enabled", "true")
                .getOrCreate();

        try {
            // Read the trip data from HDFS
            System.out.println("Reading trip data from: " + YELLOW_TRIP_DATA_PATH);
            Dataset<Row> tripData = spark.read().parquet(YELLOW_TRIP_DATA_PATH);
            
            System.out.println("Original data schema:");
            tripData.printSchema();
            System.out.println("Original data count: " + tripData.count());
            
            // Analysis 1: Filter trips with passengers > 2 and distance > 5 miles
            // Calculate duration in minutes and sort by duration descending
            Dataset<Row> longTrips = tripData
                    .filter(col("passenger_count").gt(2).and(col("trip_distance").gt(5.0)))
                    .withColumn("duration_minutes", 
                        expr("(unix_timestamp(tpep_dropoff_datetime) - unix_timestamp(tpep_pickup_datetime)) / 60"))
                    .orderBy(col("duration_minutes").desc());

            System.out.println("Filtered data count: " + longTrips.count());
            System.out.println("Sample of filtered data:");
            longTrips.show(10);

            // Save the result to HDFS
            String outputPath = OUTPUT_PATH + "q1_long_trips.parquet";
            System.out.println("Saving results to: " + outputPath);
            
            longTrips.write()
                    .mode("overwrite")
                    .parquet(outputPath);

            System.out.println("Analysis 1 completed successfully!");
            System.out.println("Results saved to: " + outputPath);

        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }
}
