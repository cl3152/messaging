package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import jakarta.jms.Queue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.jms.core.JmsTemplate;
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

    @InjectMocks
    private QueueListener queueListener;

    @MockBean
    private MessagingConfig.ProcessingGateway gateway;

    @Autowired
    private JmsTemplate jmsTemplate;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    //klappt nicht
/*    @Test
    public void testOnMessage() {
        String testPayload = "Hello, world!";
        Message<String> testMessage = MessageBuilder.withPayload(testPayload).build();

        jmsTemplate.convertAndSend("jms-demo", testPayload);

        verify(gateway, times(1)).sendToRouter(testMessage);
    }*/
}
