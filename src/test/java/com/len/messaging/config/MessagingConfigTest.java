package com.len.messaging.config;

import com.len.messaging.jms.MessageEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MessagingConfigTest {

    @Autowired
    private MessagingConfig.ProcessingGateway processingGateway;

    @MockBean
    private MessageEvaluator messageEvaluator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(messageEvaluator.evaluate(anyString())).thenReturn(MessageEvaluator.Action.DIREKT_VERARBEITEN);
    }

    @Test
    public void testMessageFlow() {
        // Given
        Message<String> message = MessageBuilder.withPayload("Test message").build();

        // When
        processingGateway.sendToRouter(message);

        // Then
        verify(messageEvaluator, times(1)).evaluate("Test message");
        // Additional verifications can be added as needed
    }
}
