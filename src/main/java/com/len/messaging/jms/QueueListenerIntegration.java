package com.len.messaging.jms;

import com.len.messaging.service.MetricsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
public class QueueListenerIntegration {
    private final MessageChannel inputIntegration;
    private final MetricsService metricsService;

    public QueueListenerIntegration(@Qualifier("inputIntegration") MessageChannel inputIntegration, MetricsService metricsService) {
        this.inputIntegration = inputIntegration;
        this.metricsService = metricsService;
    }

    @JmsListener(destination = "${jms.queue}", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message) {
        inputIntegration.send(MessageBuilder.withPayload(message).build());
        metricsService.incrementCounter();
    }
}
