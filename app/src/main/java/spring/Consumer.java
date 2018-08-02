package spring;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;

@Component
public class Consumer extends Thread implements Serializable {
    private Pattern SPACE = Pattern.compile(" ");

    @Autowired
    transient JavaSparkContext sc;


    @Value("#{'${badWords}'.split(',')}")
    public List<String> filterWords;

    @Value("${consumer.brokers}")
    private String brokers;

    @Value("${consumer.topics}")
    private String topics;

    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Value("${cassandra.table}")
    private String table;

    @Override
    public void run(){
        JavaStreamingContext jssc = new JavaStreamingContext(sc, Durations.seconds(2));

        Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", brokers);
        kafkaParams.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer1");

        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topicsSet, kafkaParams));


        JavaDStream<String> lines = messages.map(ConsumerRecord::value);//persist
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(SPACE.split(x)).iterator());
        JavaPairDStream<String, Integer> wordCounts = words.filter(word->!filterWords.contains(word))
                .mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((i1, i2) -> i1 + i2);
        wordCounts.foreachRDD( rdd ->javaFunctions(rdd.map(tuple2->new Word(tuple2._1,tuple2._2)))
                .writerBuilder(keyspace, table, CassandraJavaUtil.mapToRow(Word.class)).saveToCassandra()
        );
        wordCounts.print();

        jssc.start();
        try {
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
