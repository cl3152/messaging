package com.len.messaging.config;

import com.len.messaging.jms.MessageEvaluator;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MessagingConfigTest.TestConfig.class, MessagingConfig.class})
public class MessagingConfigTest {

    @Autowired
    private MessagingConfig.ProcessingGateway processingGateway;

    @Autowired
    private MessageChannel inputChannel;

    @Autowired
    private MessageChannel processingChannel;

    @Autowired
    private MessageChannel errorChannel;

    @Autowired
    private MessageChannel delayingChannel;

    @Autowired
    private MessageChannel transformingChannel;

    private TestMessageHandler processingHandler;
    private TestMessageHandler errorHandler;

    @BeforeEach
    public void setUp() {
        processingHandler = new TestMessageHandler();
        errorHandler = new TestMessageHandler();

        ((DirectChannel) processingChannel).subscribe(processingHandler);
        ((DirectChannel) errorChannel).subscribe(errorHandler);
    }

    @Test
    public void testDirectProcessing() {
        Message<String> message = MessageBuilder.withPayload("<?xml version=\"1.0\"?><data>direct</data>").build();
        inputChannel.send(message);
        assertThat(processingHandler.getLastMessage().getPayload()).isEqualTo("<?xml version=\"1.0\"?><data>direct</data>");
    }

    @Test
    public void testErrorProcessing() {
        Message<String> message = MessageBuilder.withPayload("error").build();
        inputChannel.send(message);
        assertThat(errorHandler.getLastMessage().getPayload()).isEqualTo("error");
    }

    @Test
    public void testDelayingProcessing() {
        Message<String> message = MessageBuilder.withPayload("<?xml version=\"1.0\"?><data>delay</data>").build();
        inputChannel.send(message);
        assertThat(processingHandler.getLastMessage()).isNull();
    }
    @Configuration
    public static class TestConfig {

        @Bean
        public MessageChannel testProcessingChannel() {
            return new DirectChannel();
        }

        @Bean
        public MessageChannel testErrorChannel() {
            return new DirectChannel();
        }

        @Bean
        public MessageChannel testDelayingChannel() {
            return new DirectChannel();
        }

        @Bean
        public MessageChannel testTransformingChannel() {
            return new DirectChannel();
        }

        @Bean
        public MessageChannel testInputChannel() {
            return new DirectChannel();
        }

        @Bean
        public MessageEvaluator messageEvaluator() {
            return new MessageEvaluator();
        }

        @Bean
        @ServiceActivator(inputChannel = "testProcessingChannel")
        public TestMessageHandler processingHandler() {
            return new TestMessageHandler();
        }

        @Bean
        @ServiceActivator(inputChannel = "testErrorChannel")
        public TestMessageHandler errorHandler() {
            return new TestMessageHandler();
        }
    }


    @Getter
    public static class TestMessageHandler implements MessageHandler {
        private Message<?> lastMessage;

        @Override
        public void handleMessage(Message<?> message) {
            this.lastMessage = message;
        }

    }
}
