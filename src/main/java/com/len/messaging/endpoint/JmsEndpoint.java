package com.len.messaging.endpoint;

import com.len.messaging.jms.JmsMetrics;
import com.len.messaging.service.MetricsService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
// mit eingebetteten JMS-Broker schlaegt die test-queue-size fehl
@Component
@Endpoint(id = "jms")
public class JmsEndpoint {
    private final JmsMetrics jmsMetrics;
    private final MetricsService metricsService;

    public JmsEndpoint(JmsMetrics jmsMetrics, MetricsService metricsService) {
        this.jmsMetrics = jmsMetrics;
        this.metricsService = metricsService;
    }
    @ReadOperation
    public Map<String, Object> jmsMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("test-queue-size", jmsMetrics.getQueueSize("jms-demo"));
        metrics.put("custom-counter", metricsService.getCounterValue());
        return metrics;
    }
}
