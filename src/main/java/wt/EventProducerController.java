package wt;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.web.internal.ThreadContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import reactor.core.publisher.Sinks;

@RestController
public class EventProducerController {

    public static final Logger LOGGER = LoggerFactory.getLogger(EventProducerController.class);


    @Autowired
    private Sinks.Many<Message<String>> many;

    @PostMapping("/messages")
    public ResponseEntity<String> sendMessage(@RequestBody JsonNode content) {
        try {
            String traceparent = content.at("/traceparent").asText();
            String message = content.at("/message").asText();
            String[] otCorrelation = ContextUtility.getCorrelationId();
            Payload response = new Payload();
            response.aiCorrelationId = ContextUtility.getAICorrelationId();
            response.otCorrelationId = otCorrelation[0];
            response.otSpandId = otCorrelation[1];
            response.message = message;
            response.traceParent = traceparent;
            String[] traceParts = traceparent.split("-");
            if (traceParts.length > 1) {
                response.parentId = traceParts[2];
                response.clientProvided = true;
            } else {
                response.parentId = null;
            }

            many.emitNext(MessageBuilder.withPayload(response.toString()).build(), Sinks.EmitFailureHandler.FAIL_FAST);

            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
