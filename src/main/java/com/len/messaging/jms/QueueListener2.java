package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

//@Component
public class QueueListener2 {

    private final MessagingConfig.ProcessingGateway gateway;

    @Autowired
    public QueueListener2(MessagingConfig.ProcessingGateway gateway) {
        this.gateway = gateway;
    }

/*    @JmsListener(destination = "${apress.jms.queue}")
    public void onMessage(Message<String> message) {
        gateway.sendToRouter(message);
    }*/



    //Brauch ich einen try catch Block oder greift der globale Error handler?
    @JmsListener(destination = "${apress.jms.queue}")
    public void onMessage(TextMessage jmsMessage) throws JMSException {
        String payload = jmsMessage.getText();

        // Simuliere einen Fehler, um das Transaktionsmanagement zu testen
/*        if (payload.contains("trigger-error")) {
            throw new RuntimeException("Simulierter Fehler zur Überprüfung des Transaktionsmanagements");
        }*/

        Message<String> message = MessageBuilder.withPayload(payload)
                .setHeader("jms_message", jmsMessage)
                .build();

        gateway.sendToRouter(message);

        System.out.println("Message automatisch acknowledged!!!");
    }


}
