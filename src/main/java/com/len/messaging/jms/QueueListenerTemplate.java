

package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import com.len.messaging.repository.ArbeitnehmerRepository;
import com.len.messaging.repository.TransferRepository;
import com.len.messaging.service.ArbeitnehmerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

// Verwendung des TransactionTemplates
// @Component
public class QueueListenerTemplate {

    private final MessagingConfig.ProcessingGateway gateway;
    private final TransactionTemplate transactionTemplate;


    @Autowired
    public QueueListenerTemplate(MessagingConfig.ProcessingGateway gateway, @Qualifier("jmsTransactionTemplate") TransactionTemplate transactionTemplate) {
        this.gateway = gateway;
        this.transactionTemplate = transactionTemplate;
    }

    @JmsListener(destination = "jms-demo", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message) {
        transactionTemplate.execute(status -> {
            try {
                System.out.println("Received message: " + message);

               Message<String> buildmessage = MessageBuilder.withPayload(message)
                        .build();

                 gateway.sendToInputIntegration(buildmessage);

                if (message.contains("trigger-error")) {
                    throw new RuntimeException("Simulierter Fehler zur Überprüfung des Transaktionsmanagements");
                }

            } catch (Exception e) {
                status.setRollbackOnly();
                System.err.println("Error processing message: " + e.getMessage());
            }
            return null;
        });
    }

}


