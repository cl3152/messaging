package com.len.messaging.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.ErrorHandler;

//@Configuration
@EnableConfigurationProperties(JMSProperties.class)
@EnableJms
public class JMSConfig2 {

     private static final Logger logger = LoggerFactory.getLogger(JMSConfig2.class);

     /** Ziel: lokale JMS-Transaktion
      * */
     @Bean
     public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory, @Qualifier("jmsErrorHandler") ErrorHandler errorHandler) {
          DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
          factory.setConnectionFactory(connectionFactory);
          factory.setConcurrency("1-20");
          factory.setSessionTransacted(true); // Aktiviert lokale Transaktionen


          if (connectionFactory instanceof CachingConnectionFactory) {
               CachingConnectionFactory cachingConnectionFactory = (CachingConnectionFactory) connectionFactory;
               if (cachingConnectionFactory.getTargetConnectionFactory() instanceof ActiveMQConnectionFactory) {
                    ActiveMQConnectionFactory activeMQConnectionFactory =
                            (ActiveMQConnectionFactory) cachingConnectionFactory.getTargetConnectionFactory();
                    RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
                    redeliveryPolicy.setMaximumRedeliveries(4);
                    redeliveryPolicy.setInitialRedeliveryDelay(1000);
                    redeliveryPolicy.setRedeliveryDelay(1000);
                    redeliveryPolicy.setUseExponentialBackOff(false);
                    activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
               }
          }

          factory.setErrorHandler(errorHandler);

          return factory;
     }

     @Bean
     public MessageChannel errorChannel() {
          return new DirectChannel();
     }

     // https://www.baeldung.com/spring-jms
     @Bean
     public ErrorHandler jmsErrorHandler(MessageChannel errorChannel) {
          return t -> {
               logger.error("Fehler im JMS Listener aufgetreten: ", t);

               // Nachricht an den errorChannel senden
               Message<String> errorMessage = MessageBuilder.withPayload(t.getMessage())
                       .setHeader("error", t.getMessage())
                       .build();
               errorChannel.send(errorMessage);
          };
     }


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