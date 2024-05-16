package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QueueListener {

    @Autowired
    private MessagingConfig.ProcessingGateway gateway;

    @Transactional
    @JmsListener(destination = "${apress.jms.queue}")
    public void onMessage(Message<String> message) {
        gateway.sendToRouter(message);
    }
}
