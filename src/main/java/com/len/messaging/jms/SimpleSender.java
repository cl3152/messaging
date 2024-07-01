package com.len.messaging.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;


// Wird in der Main-Methode zum Senden der XML in die Queue verwendet.
@Component
public class SimpleSender {

    private final JmsTemplate jmsTemplate;

    @Autowired
    public SimpleSender(JmsTemplate jmsTemplate){
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String destination, String message){
        this.jmsTemplate.convertAndSend(destination, message);
    }
}