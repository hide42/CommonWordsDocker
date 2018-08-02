package spring;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:application.yml")
public class ApplicationConfig {

    @Value("${app.name}")
    private String appName;

    @Value("${master.uri}")
    private String masterUri;

    @Value("${cassandra.docker-host}")
    private String host;

    @Bean
    public SparkConf sparkConf() {
        SparkConf sparkConf = new SparkConf()
                .setAppName(appName)
                .setMaster("local[2]")
                .set("spark.cassandra.connection.host", host)
                .set("spark.cassandra.connection.port", "9042");

        return sparkConf;
    }
    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }
}
