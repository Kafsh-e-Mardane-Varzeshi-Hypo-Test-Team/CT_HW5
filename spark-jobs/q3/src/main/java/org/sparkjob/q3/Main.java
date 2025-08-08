package org.sparkjob.q3;

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
                .appName("Analysis 3 - Revenue and Trip Count by Destination Borough")
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
            
            // Analysis 3: Total revenue and trip count by destination borough
            // Join trip data with zone lookup on DOLocationID = LocationID (destination)
            // Group by Borough, calculate total revenue and trip count
            Dataset<Row> revenueByBorough = tripData
                    .join(zoneLookup, tripData.col("DOLocationID").equalTo(zoneLookup.col("LocationID")))
                    .groupBy("Borough")
                    .agg(
                        sum("total_amount").as("total_revenue"),
                        count("*").as("trip_count")
                    )
                    .orderBy("Borough");

            System.out.println("Joined data count: " + revenueByBorough.count());
            System.out.println("Revenue and trip count by borough:");
            revenueByBorough.show(10);

            // Save the result to HDFS
            String outputPath = OUTPUT_PATH + "q3_revenue_by_borough.parquet";
            System.out.println("Saving results to: " + outputPath);
            
            revenueByBorough.write()
                    .mode("overwrite")
                    .parquet(outputPath);

            System.out.println("Analysis 3 completed successfully!");
            System.out.println("Results saved to: " + outputPath);

        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }
}
