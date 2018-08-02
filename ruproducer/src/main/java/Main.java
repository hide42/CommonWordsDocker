import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        //try to create topic
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"kafka1:9092");

        AdminClient admin = AdminClient.create(config);

        NewTopic newTopic = new NewTopic("test1",1,(short) 1);
        CreateTopicsResult createTopic = admin.createTopics(Collections.singleton(newTopic));
        KafkaFuture<Void> all = createTopic.all();
        try{all.get();}catch (Exception e){
            System.out.println("error");
        }
        System.err.println("topic created");


        new Producer("kafka1:9092").run();
        //Files.walk(Paths.get("")).filter(Files::isRegularFile).forEach(System.err::println);
    }
}
