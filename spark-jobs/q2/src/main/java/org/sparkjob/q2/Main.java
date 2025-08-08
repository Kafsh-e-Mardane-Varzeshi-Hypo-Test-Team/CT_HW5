package org.sparkjob.q2;

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
                .appName("Analysis 2 - Average Fare by Pickup Zone")
                .config("spark.sql.adaptive.enabled", "true")
                .getOrCreate();

        try {
            // Read the trip data from HDFS
            System.out.println("Reading trip data from: " + YELLOW_TRIP_DATA_PATH);
            Dataset<Row> tripData = spark.read().parquet(YELLOW_TRIP_DATA_PATH);
            
            // Read the zone lookup data from HDFS
            System.out.println("Reading zone lookup data from: " + TAXI_ZONE_LOOKUP_PATH);
            Dataset<Row> zoneLookup = spark.read()
                    .option("header", "true")
                    .csv(TAXI_ZONE_LOOKUP_PATH);
            
            System.out.println("Trip data schema:");
            tripData.printSchema();
            System.out.println("Zone lookup schema:");
            zoneLookup.printSchema();
            
            System.out.println("Original trip data count: " + tripData.count());
            System.out.println("Zone lookup data count: " + zoneLookup.count());
            
            // Analysis 2: Average fare by pickup zone
            // Join trip data with zone lookup on PULocationID = LocationID
            // Group by Zone and Borough, calculate average fare_amount
            Dataset<Row> avgFareByZone = tripData
                    .join(zoneLookup, tripData.col("PULocationID").equalTo(zoneLookup.col("LocationID")))
                    .groupBy("Zone", "Borough")
                    .agg(avg("fare_amount").as("avg_fare_amount"))
                    .orderBy("Zone");

            System.out.println("Joined data count: " + avgFareByZone.count());
            System.out.println("Sample of average fare by zone:");
            avgFareByZone.show(10);

            // Save the result to HDFS
            String outputPath = OUTPUT_PATH + "q2_avg_fare_by_zone.parquet";
            System.out.println("Saving results to: " + outputPath);
            
            avgFareByZone.write()
                    .mode("overwrite")
                    .parquet(outputPath);

            System.out.println("Analysis 2 completed successfully!");
            System.out.println("Results saved to: " + outputPath);

        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }
}
