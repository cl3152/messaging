package com.len.messaging.config;

import com.len.messaging.jms.MessageEvaluator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.test.context.MockIntegrationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.integration.test.mock.MockIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
//@SpringIntegrationTest
public class MessagingConfigTest {

    @Autowired
    private MessagingConfig messagingConfig;

    @Autowired
    private MessagingConfig.ProcessingGateway processingGateway;


/*    @MockBean
    private MockIntegrationContext mockIntegrationContext;*/


    @MockBean
    private MessageEvaluator messageEvaluator;

    @Autowired
    @Qualifier("processingChannel")
    MessageChannel testProcessingChannel;

/*    @Mock
    private MessageHandler mockHandler;*/

/*    @Captor
    private ArgumentCaptor<Message<String>> messageCaptor;*/

    private AutoCloseable mocks;

 //   private PollableChannel outputChannel;

    @BeforeEach
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        when(messageEvaluator.evaluate(anyString())).thenReturn(MessageEvaluator.Action.DIREKT_VERARBEITEN);
//        ((DirectChannel) testProcessingChannel).subscribe(mockHandler);
/*        outputChannel = new QueueChannel();
        mockIntegrationContext.substituteMessageHandlerFor("messagingConfig.processingInput", message -> {
            outputChannel.send(new GenericMessage<>("processed: " + message.getPayload()));
        });*/
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void testInputChannel() {
        // Given
        Message<String> message = MessageBuilder.withPayload("Test message").build();
        // When
        processingGateway.sendToRouter(message);
        // Then
        verify(messageEvaluator, times(1)).evaluate("Test message");
    }


/*
    Hierfür muss im Setup auf einen outputChannel umgelenkt werden.
    @Test
    public void testProcessingFlow() {
        // Send a message through the gateway
        // Given
        Message<String> message = MessageBuilder.withPayload("Test message").build();
        processingGateway.sendToRouter(message);

        // Verify the processed message
        Message<?> receivedMessage = outputChannel.receive(0);
        assertNotNull(receivedMessage, "Message should have been received.");
        assertEquals("processed: HELLO WORLD", receivedMessage.getPayload());
    }*/

/*    @Test
    public void testProcessingFlow2() {
        // Given
        Message<String> message = MessageBuilder.withPayload("hello world").build();
        doNothing().when(mockHandler).handleMessage(any(Message.class));

        processingGateway.sendToRouter(message);

        // Then
        verify(mockHandler).handleMessage(messageCaptor.capture());
        Message<String> receivedMessage = messageCaptor.getValue();
        assertNotNull(receivedMessage, "Message should have been received by the handler.");
        assertEquals("HELLO WORLD", receivedMessage.getPayload());
    }*/


/*    @Test
    public void testFilterReject() {
        // Send a message that should be filtered out
        processingGateway.sendToRouter("hi");

        // Verify that no message was received
        Message<?> receivedMessage = outputChannel.receive(1000);
        assertNull(receivedMessage, "No message should have been received.");
    }

    @Test
    public void testTransformation() {
        // Send a message through the input channel directly to test the transformer
        inputChannel.send(new GenericMessage<>("test message"));

        // Verify the transformed message in the processing channel
        Message<?> receivedMessage = processingChannel.receive(1000);
        assertNotNull(receivedMessage, "Message should have been received.");
        assertEquals("TEST MESSAGE", receivedMessage.getPayload());
    }*/

/*    @Test
    public void testProcessingChannel() {
        // Given
        Message<String> message = MessageBuilder.withPayload("<?xml").build();

        testProcessingChannel.send(message);

        // Channel Check
        //message = (Message<String>) testProcessingChannel.receive(7000);
        verify(mockHandler).handleMessage(messageCaptor.capture());
        Message<String> receivedMessage = messageCaptor.getValue();
        assertNotNull(receivedMessage, "Message should have been received within the timeout period.");
        assertEquals("<?xml", receivedMessage.getPayload());
    }*/

    @Test
    public void testProcessingInput() {
        Message<String> message = MessageBuilder.withPayload("<?xml").build();
        messagingConfig.processingInput(message);
        // e.g. check the call of a service
        // verify(messageService, times(1)).processMessage("<?xml");
    }




}
