/*
package com.len.messaging.jms;

import com.len.messaging.config.MessagingConfig;
import com.len.messaging.jms.QueueListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MessagingConfig.class, QueueListener.class, MessageEvaluator.class})
public class JmsClientAcknowledgeIntegrationTest {

    @MockBean
    private JmsTemplate jmsTemplate;

    @MockBean
    private MessagingConfig.ProcessingGateway processingGateway;

    @Captor
    private ArgumentCaptor<Message<String>> messageCaptor;

    @Mock
    private TextMessage jmsMessage;

    @Test
    public void testJmsMessageListenerWithClientAcknowledge() throws JMSException {
        // Given
        String testMessage = "Test message";

        // Mock the JMS message
        when(jmsMessage.getText()).thenReturn(testMessage);

        // When
        jmsTemplate.send("jms-demo", session -> jmsMessage);

        // Then
        verify(processingGateway, timeout(5000).times(1)).sendToRouter(messageCaptor.capture());
        Message<String> capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertTrue(capturedMessage.getPayload().contains(testMessage));

        // Verify that the acknowledge method was called
        verify(jmsMessage, times(1)).acknowledge();
    }
}
*/
