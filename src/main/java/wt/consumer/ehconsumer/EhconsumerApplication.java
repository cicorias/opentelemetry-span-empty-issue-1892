package wt.consumer.ehconsumer;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

import com.azure.spring.integration.core.EventHubHeaders;
import com.azure.spring.integration.core.api.reactor.Checkpointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

import static com.azure.spring.integration.core.AzureHeaders.CHECKPOINTER;

@SpringBootApplication
public class EhconsumerApplication {

	public static final Logger LOGGER = LoggerFactory.getLogger(EhconsumerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EhconsumerApplication.class, args);
	}

    public String getCorrelationId() {

        Span current = null;
        SpanContext context = null;
        String traceid = null;

        current = Span.current();

        if (null != current)
            context = current.getSpanContext();

        if (null != current && null != context)
            traceid = context.getTraceId();
        

        return traceid;


        // return Span.current() != null && Span.current().getSpanContext() != null ? Span.current().getSpanContext().getTraceId() : null;

        // Span.current()
    // PropagatedSpan@231 "PropagatedSpan{ImmutableSpanContext{traceId=00000000000000000000000000000000, 
    //spanId=0000000000000000, traceFlags=00, traceState=ArrayBasedTraceState{entries=[]}, remote=false, valid=false}}"
      }

	@Bean
    public Consumer<Message<String>> consume() {
        return message -> {
            Checkpointer checkpointer = (Checkpointer) message.getHeaders().get(CHECKPOINTER);


            var thing = getCorrelationId();


            LOGGER.warn("**********  correlationid={}", thing);

            LOGGER.warn("New message received: '{}', partition key: {}, sequence number: {}, offset: {}, enqueued time: {}",
                // message.getPayload(),
				message.getHeaders().get(EventHubHeaders.PARTITION_ID),
                message.getHeaders().get(EventHubHeaders.RAW_PARTITION_ID),
                message.getHeaders().get(EventHubHeaders.SEQUENCE_NUMBER),
                message.getHeaders().get(EventHubHeaders.OFFSET),
                message.getHeaders().get(EventHubHeaders.ENQUEUED_TIME)
            );
            checkpointer.success()
                        .doOnSuccess(success -> LOGGER.info("Message '{}' successfully checkpointed", message.getPayload()))
                        .doOnError(error -> LOGGER.error("Exception found", error))
                        .subscribe();
        };
    }
}
