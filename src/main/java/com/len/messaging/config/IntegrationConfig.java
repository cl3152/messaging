package com.len.messaging.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.jms.dsl.JmsMessageDrivenChannelAdapterSpec;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import jakarta.jms.Session;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
@EnableIntegration
@EnableConfigurationProperties(JMSProperties.class)
@EnableJms
public class IntegrationConfig {

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }

    @Bean
    public MessageChannel inputIntegration() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel outputIntegration() {
        return new DirectChannel();
    }

    @Bean
    public DefaultMessageListenerContainer messageListenerContainer() {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setDestinationName("sourceQueue");
        container.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return container;
    }

    @Bean
    public JmsMessageDrivenChannelAdapterSpec<?> jmsMessageDrivenChannelAdapter() {
        return Jms.messageDrivenChannelAdapter(messageListenerContainer())
                .outputChannel(inputIntegration());
    }

    @Bean
    @ServiceActivator(inputChannel = "inputIntegration")
    public MessageHandler messageHandler() {
        return message -> {
            try {
                String payload = (String) message.getPayload();
                String newPayload = "Geänderter Inhalt: " + payload;
                System.out.println(newPayload);

                Message<String> newMessage = MessageBuilder
                        .withPayload(newPayload)
                        .copyHeaders(message.getHeaders())
                        .build();

                MessagingTemplate messagingTemplate = new MessagingTemplate();
                messagingTemplate.send(outputIntegration(), newMessage);

                if (message.getHeaders().get("jms_message") instanceof jakarta.jms.Message) {
                    jakarta.jms.Message jmsMessage = (jakarta.jms.Message) message.getHeaders().get("jms_message");
                    assert jmsMessage != null;
                    jmsMessage.acknowledge();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "outputIntegration")
    public MessageHandler jmsOutboundGateway(JmsTemplate jmsTemplate) {
        return message -> {
            String payload = (String) message.getPayload();
            jmsTemplate.convertAndSend("destinationQueue", payload);
        };
    }
}
