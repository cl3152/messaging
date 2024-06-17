package com.len.messaging;

import com.len.messaging.jms.JmsMetrics;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "jms")
public class JmsEndpoint {

    private final JmsMetrics jmsMetrics;

    public JmsEndpoint(JmsMetrics jmsMetrics) {
        this.jmsMetrics = jmsMetrics;
    }

    @ReadOperation
    public Map<String, Object> jmsMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("test-queue-size", jmsMetrics.getQueueSize("jms-demo"));
        return metrics;
    }
}
