package wt;

import static com.azure.spring.integration.core.AzureHeaders.CHECKPOINTER;

import java.util.function.Consumer;

import com.azure.spring.integration.core.EventHubHeaders;
import com.azure.spring.integration.core.api.reactor.Checkpointer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;

@Configuration
@Profile("consumer")
public class EventConsumer {
  public static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
  
  @Bean
  public Consumer<Message<Payload>> consume() {
      return message -> {
          Checkpointer checkpointer = (Checkpointer) message.getHeaders().get(CHECKPOINTER);

          String[] otCorrelation = ContextUtility.getCorrelationId();

          LOGGER.warn("**********  correlationid={}  spanid={}", otCorrelation[0], otCorrelation[1]);

          LOGGER.warn(
                  "New message received: '{}', diagnostic-id: {},  partition key: {}, partition id: {}, raw partition id: {}, sequence number: {}, offset: {}, enqueued time: {}",
                  message.getPayload(),
                  message.getHeaders().get("diagnostic-id"),
                  message.getHeaders().get(EventHubHeaders.PARTITION_KEY),
                  message.getHeaders().get(EventHubHeaders.PARTITION_ID),
                  message.getHeaders().get(EventHubHeaders.RAW_PARTITION_ID),
                  message.getHeaders().get(EventHubHeaders.SEQUENCE_NUMBER),
                  message.getHeaders().get(EventHubHeaders.OFFSET),
                  message.getHeaders().get(EventHubHeaders.ENQUEUED_TIME));
          checkpointer.success()
                  .doOnSuccess(success -> LOGGER.info("Message with diagnostic-id={} successfully checkpointed", message.getHeaders().get("diagnostic-id")))
                  .doOnError(error -> LOGGER.error("Exception found", error)).subscribe();
      };
  }
}
