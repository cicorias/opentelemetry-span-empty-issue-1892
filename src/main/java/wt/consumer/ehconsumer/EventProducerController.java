package wt.consumer.ehconsumer;


import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

import java.beans.ConstructorProperties;

import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.web.internal.ThreadContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Sinks;

@RestController
public class EventProducerController {

    public static final Logger LOGGER = LoggerFactory.getLogger(EventProducerController.class);

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
    }

    public String getAICorrelationId() {
        var requestTelemetryContext = ThreadContext.getRequestTelemetryContext();
        RequestTelemetry requestTelemetry = requestTelemetryContext == null ? null : requestTelemetryContext.getHttpRequestTelemetry();
        String correlationId = requestTelemetry == null ? null : requestTelemetry.getContext().getOperation().getId();

        return correlationId;
    }


    @Autowired
    private Sinks.Many<Message<String>> many;

    @PostMapping("/messages")
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        LOGGER.info("Going to add message {} to sendMessage.", message);

        var response = new Response();
        response.aiCorrelationId = getAICorrelationId();
        response.otCorrelationId = getCorrelationId();

        many.emitNext(MessageBuilder.withPayload(message).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        return ResponseEntity.ok(response.toString());
    }

    class Response{
        public String aiCorrelationId;
        public String otCorrelationId;
        @Override
        public String toString() {
            return String.format("{\"aiCorrelationId\": \"%s\" ,\n\"otCorrelationId\": \"%s\"}", aiCorrelationId, otCorrelationId);
        }
    }
}
