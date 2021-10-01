package wt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class Payload {
    /**
     *
     */
    final ObjectMapper mapper = new ObjectMapper();


    public boolean clientProvided = false;
    public String aiCorrelationId;
    public String otCorrelationId;
    public String otSpandId;
    public String traceParent;
    public String parentId;
    public String message;

    @Override
    public String toString() {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            EventProducerController.LOGGER.error("could not convert Response to json", e);
            return "Payload [aiCorrelationId=" + aiCorrelationId + ", clientProvided=" + clientProvided + ", message="
            + message + ", otCorrelationId=" + otCorrelationId + ", otSpandId=" + otSpandId + ", parentId="
            + parentId + ", traceParent=" + traceParent + "]";
        }
    }
}