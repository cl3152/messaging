package com.len.messaging.config;

import com.len.messaging.jms.QueueListener;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.util.ErrorHandler;

@Configuration
@EnableConfigurationProperties(JMSProperties.class)
@EnableJms
public class JMSConfig {


    /** This is for the QueueListener
     */
/*
	@Bean
	public DefaultMessageListenerContainer customMessageListenerContainer(
            ConnectionFactory connectionFactory, MessageListener queueListener, @Value("${apress.jms.queue}") final String destinationName){
		DefaultMessageListenerContainer listener = new DefaultMessageListenerContainer();
		listener.setConnectionFactory(connectionFactory);
		listener.setDestinationName(destinationName);
		listener.setMessageListener(queueListener);
		return listener;
	}
*/


    /* This is necessary when you want to send and Object without using Serializable.*/
/*    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_class_"); // This value can be anything, it will save the JSON class name and it must be the same for sender/receiver
        return converter;
    }*/





    /*** This section is another way to customize a Connection Factory and the Message Listener by implementing the JmsListenerConfigurer
     @Bean
     public DefaultJmsListenerContainerFactory customFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
     DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
     configurer.configure(factory, connectionFactory);
     return factory;
     }

     @Configuration
     public static class ListenerConfig implements JmsListenerConfigurer{

     private MessageListener queueListener;
     private String destinationName;

     public ListenerConfig(MessageListener queueListener, @Value("${apress.jms.queue}") final String destinationName){
     this.queueListener = queueListener;
     this.destinationName = destinationName;
     }

     @Override
     public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
     SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
     endpoint.setId(UUID.randomUUID().toString());
     endpoint.setDestination(this.destinationName);
     endpoint.setMessageListener(this.queueListener);
     registrar.registerEndpoint(endpoint);
     }
     }
     ***/

}