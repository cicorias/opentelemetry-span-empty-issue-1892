package wt;

import static com.azure.spring.integration.core.AzureHeaders.CHECKPOINTER;

import java.util.function.Consumer;

import com.azure.spring.integration.core.EventHubHeaders;
import com.azure.spring.integration.core.api.reactor.Checkpointer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;

@Profile("!unittest") // Dont run during unit tests
@SpringBootApplication
public class EhconsumerApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(EhconsumerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EhconsumerApplication.class, args);
    }

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
                    .doOnSuccess(success -> LOGGER.info("Message '{}' successfully checkpointed", message.getPayload()))
                    .doOnError(error -> LOGGER.error("Exception found", error)).subscribe();
        };
    }
}
