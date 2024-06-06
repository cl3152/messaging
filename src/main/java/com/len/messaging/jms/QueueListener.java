package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//@Component
public class QueueListener {
/*

    private final MessagingConfig.ProcessingGateway gateway;

    @Autowired
    public QueueListener(MessagingConfig.ProcessingGateway gateway) {
        this.gateway = gateway;
    }

*/
/*    @JmsListener(destination = "${apress.jms.queue}")
    public void onMessage(Message<String> message) {
        gateway.sendToRouter(message);
    }*//*




    //Brauch ich einen try catch Block oder greift der globale Error handler?
    @JmsListener(destination = "${apress.jms.queue}")
    public void onMessage(TextMessage jmsMessage) throws JMSException {
            String payload = jmsMessage.getText();

            Message<String> message = MessageBuilder.withPayload(payload)
                    .setHeader("jms_message", jmsMessage)
                    .build();

            gateway.sendToRouter(message);

            jmsMessage.acknowledge();

            System.out.println("Message sollte acknowledged sein.");
    }
*/


}
