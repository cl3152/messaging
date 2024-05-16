package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class QueueListenerIntegrationtest {

    @Autowired
    private QueueListener queueListener;

    @MockBean
    private MessagingConfig.ProcessingGateway gateway;

    @Test
    public void testOnMessage() {
        String testPayload = "Hello, world!";
        Message<String> testMessage = MessageBuilder.withPayload(testPayload).build();

        queueListener.onMessage(testMessage);

        verify(gateway, times(1)).sendToRouter(testMessage);
    }
}
