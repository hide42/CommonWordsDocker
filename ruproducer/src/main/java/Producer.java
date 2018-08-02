import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;


/**
 * Spring potential is not used for separate use
 * (Another machine)
 */
public class Producer extends Thread{

  private static String BOOTSTRAP_SERVER;
  private static final String ACKS = "all";
  private static final long BUFFER_MEMORY = 33554444;
  private static final int RETRIES = 0;
  private static final int BATCH_SIZE = 55555;
  private static final String CLIENT_ID = "producer1";
  private static final long LINGER_MS = 1;

  private KafkaProducer<Long, String> producer;

  public Producer(String topic) {
    BOOTSTRAP_SERVER = topic;
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.ACKS_CONFIG, ACKS);
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, BUFFER_MEMORY);
    props.put(ProducerConfig.RETRIES_CONFIG, RETRIES);
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, BATCH_SIZE);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
    props.put(ProducerConfig.LINGER_MS_CONFIG, LINGER_MS);
    producer = new KafkaProducer<>(props);
  }
  @Override
  public void run() {

      String fileName = "/var/lib/cache/list.txt";
      try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
          stream.map(x->x.replaceAll("\\s+|\"", " ").trim())
                  .forEach(music-> producer.send(new ProducerRecord<>(
                          "test1",
                          System.nanoTime(),
                          TextHelper.getText(music))));
      } catch (IOException e) {
          e.printStackTrace();
      }

  }

}
