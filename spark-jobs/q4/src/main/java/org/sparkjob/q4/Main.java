package org.sparkjob.q4;

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
                .appName("Analysis 4 - Maximum Tip Amount per Day")
                .config("spark.sql.adaptive.enabled", "true")
                .getOrCreate();

        try {
            // Read the trip data from HDFS
            System.out.println("Reading trip data from: " + YELLOW_TRIP_DATA_PATH);
            Dataset<Row> tripData = spark.read().parquet(YELLOW_TRIP_DATA_PATH);
            
            System.out.println("Trip data schema:");
            tripData.printSchema();
            System.out.println("Original trip data count: " + tripData.count());
            
            // Show sample data to understand the datetime format
            System.out.println("Sample trip data with pickup datetime:");
            tripData.select("tpep_pickup_datetime", "tip_amount").show(5);
            
            // Analysis 4: Maximum tip amount per day
            // Extract date from pickup datetime and calculate maximum tip per day
            Dataset<Row> maxTipPerDay = tripData
                    .withColumn("pickup_date", date_format(col("tpep_pickup_datetime"), "yyyy-MM-dd"))
                    .groupBy("pickup_date")
                    .agg(max("tip_amount").as("max_tip_amount"))
                    .orderBy("pickup_date");

            System.out.println("Number of unique days: " + maxTipPerDay.count());
            System.out.println("Maximum tip amount per day:");
            maxTipPerDay.show(10);

            // Show some statistics about the results
            System.out.println("Summary statistics of maximum tips per day:");
            maxTipPerDay.describe("max_tip_amount").show();

            // Save the result to HDFS
            String outputPath = OUTPUT_PATH + "q4_max_tip_per_day.parquet";
            System.out.println("Saving results to: " + outputPath);
            
            maxTipPerDay.write()
                    .mode("overwrite")
                    .parquet(outputPath);

            System.out.println("Analysis 4 completed successfully!");
            System.out.println("Results saved to: " + outputPath);

        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.stop();
        }
    }
}
