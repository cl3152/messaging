/*
package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

//@Component
public class QueueListener3 {

    private final MessagingConfig.ProcessingGateway gateway;
    private final JmsTemplate jmsTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public QueueListener3(MessagingConfig.ProcessingGateway gateway, JmsTemplate jmsTemplate, @Qualifier("jmsTransactionTemplate") TransactionTemplate transactionTemplate) {
        this.gateway = gateway;
        this.jmsTemplate = jmsTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @JmsListener(destination = "jms-demo", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message) {
        transactionTemplate.execute(status -> {
            try {
                System.out.println("Received message: " + message);
                if (message.contains("trigger-error")) {
            throw new RuntimeException("Simulierter Fehler zur Überprüfung des Transaktionsmanagements");
        }

                sendMessage("responseQueue", "Processed: " + message);

            } catch (Exception e) {
                status.setRollbackOnly();
                System.err.println("Error processing message: " + e.getMessage());
            }
            return null; // Return null since execute expects a return value
        });
    }

    public void sendMessage(String destination, String response) {
        jmsTemplate.convertAndSend(destination, response);
    }
}
*/
