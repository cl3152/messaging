package com.len.messaging.config;

import com.len.messaging.jms.MessageEvaluator;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@Profile("test")
public class TestMessagingConfig {

    @Bean
    public BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector("vm://localhost");
        return broker;
    }


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


    public static class TestMessageHandler implements MessageHandler {
        private Message<?> lastMessage;

        @Override
        public void handleMessage(Message<?> message) {
            this.lastMessage = message;
        }

        public Message<?> getLastMessage() {
            return lastMessage;
        }
    }


}
