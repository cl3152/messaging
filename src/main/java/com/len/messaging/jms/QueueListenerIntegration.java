package com.len.messaging.jms;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

//@Configuration
public class QueueListenerIntegration {
    private final MessageChannel inputIntegration;

    public QueueListenerIntegration(@Qualifier("inputIntegration") MessageChannel inputIntegration) {
        this.inputIntegration = inputIntegration;
    }

    @JmsListener(destination = "${jms.queue}", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message) {
        inputIntegration.send(MessageBuilder.withPayload(message).build());
    }
}
