package com.len.messaging.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter customCounter;

    public MetricsService(MeterRegistry meterRegistry) {
        this.customCounter = meterRegistry.counter("custom.counter", "type", "jms-demo");
    }

    public void incrementCounter() {
        customCounter.increment();
    }

    public double getCounterValue() {
        return customCounter.count();
    }
}
