package spring;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import scala.Tuple2;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;

@SpringBootApplication
@RestController
public class DemoApplication {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    Consumer consumer;

    @Autowired
    JavaSparkContext javaSparkContext;

    @Value("${consumer.brokers}")
    private String broker;

    @Value("${consumer.topics}")
    private String topic;

    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Value("${cassandra.table}")
    private String table;

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {

        consumer.start();
    }

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView model = new ModelAndView("index");
        JavaRDD<Tuple2<String,Integer>> resultsRDD = javaFunctions(javaSparkContext)
                .cassandraTable(keyspace, table, CassandraJavaUtil.mapRowTo(Word.class))
                .map(word->new Tuple2(word.getWord(),word.getCount()));
        model.addObject("items",resultsRDD
                .mapToPair(Tuple2::swap)
                .sortByKey(false)
                .map(Tuple2->Tuple2._2+" "+Integer.toString(Tuple2._1))//mapToPair(Tuple2::swap) для визуализации мб
                .take(30));
        return model;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
