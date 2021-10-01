package wt;

import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.web.internal.ThreadContext;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

public class ContextUtility {

  /** this retrieves the OpenTelemetry representation of traceid */
  // broken in// implementation
  // 'com.azure.spring:azure-spring-cloud-stream-binder-eventhubs:2.9.0' // breaks
  // getting correlationid from span
  public static String[] getCorrelationId() {

    Span current = null;
    SpanContext context = null;
    String traceid = null;
    String spanid = null;

    current = Span.current();

    if (null != current) {
      context = current.getSpanContext();
    }

    if (null != current && null != context) {
      traceid = context.getTraceId();
      spanid = context.getSpanId();
    }

    return new String[] { traceid, spanid };
  }

  public static String getAICorrelationId() {
    var requestTelemetryContext = ThreadContext.getRequestTelemetryContext();
    RequestTelemetry requestTelemetry = requestTelemetryContext == null ? null
        : requestTelemetryContext.getHttpRequestTelemetry();
    String correlationId = requestTelemetry == null ? null : requestTelemetry.getContext().getOperation().getId();

    return correlationId;
  }

}
