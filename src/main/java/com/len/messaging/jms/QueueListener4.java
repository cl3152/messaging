package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import com.len.messaging.domain.Arbeitnehmer;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.domain.Transfer;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

//@Component
public class QueueListener4 {

    private final MessagingConfig.ProcessingGateway gateway;
    private final JmsTemplate jmsTemplate;
    private final TransactionTemplate transactionTemplate;

    private final TransferRepository transferRepository;
    private final ArbeitnehmerRepository arbeitnehmerRepository;

    @Autowired
    private ArbeitnehmerService arbeitnehmerService;

    @Autowired
    public QueueListener4(MessagingConfig.ProcessingGateway gateway, JmsTemplate jmsTemplate, @Qualifier("jmsTransactionTemplate") TransactionTemplate transactionTemplate, TransferRepository transferRepository, ArbeitnehmerRepository arbeitnehmerRepository) {
        this.gateway = gateway;
        this.jmsTemplate = jmsTemplate;
        this.transactionTemplate = transactionTemplate;
        this.transferRepository = transferRepository;
        this.arbeitnehmerRepository = arbeitnehmerRepository;
    }

    @JmsListener(destination = "jms-demo", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message) {
        transactionTemplate.execute(status -> {
            try {
                System.out.println("Received message: " + message);

               Message<String> buildmessage = MessageBuilder.withPayload(message)
                        .build();

            gateway.sendToRouter(buildmessage);

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



    public void sendMessage(String destination, String response) {
        jmsTemplate.convertAndSend(destination, response);
    }
}
