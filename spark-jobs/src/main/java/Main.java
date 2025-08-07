
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Main {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("HDFS Test")
                .getOrCreate();

        Dataset<Row> df = spark.read().text("hdfs://namenode1:8020/data/input.txt");
        df.show();
        df.write().text("hdfs://namenode1:8020/data/output");

    }
}
