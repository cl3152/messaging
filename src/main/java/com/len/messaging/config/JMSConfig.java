package com.len.messaging.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ErrorHandler;

@Configuration
@EnableConfigurationProperties(JMSProperties.class)
@EnableJms
public class JMSConfig {
     @Bean
     public ConnectionFactory connectionFactory() {
          ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
          RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
          redeliveryPolicy.setMaximumRedeliveries(4);
          redeliveryPolicy.setInitialRedeliveryDelay(1000);
          redeliveryPolicy.setRedeliveryDelay(1000);
          redeliveryPolicy.setUseExponentialBackOff(false);
          activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);

          return new CachingConnectionFactory(activeMQConnectionFactory);
     }

     @Bean
     public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory){
          DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
          factory.setConnectionFactory(connectionFactory);
          factory.setConcurrency("1-20");
          factory.setSessionTransacted(true);
          return factory;
     }

/*   Zeigt, wie die Konfiguration aussieht, wenn ein PlatformTransactionManager, ein ErrorHandler und das TransactionTemplate verwendet werden soll
     @Bean
     public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
             ConnectionFactory connectionFactory,
             @Qualifier("jmsErrorHandler") ErrorHandler errorHandler,
             @Qualifier("jmsTransactionManager") PlatformTransactionManager jmsTransactionManager) {
          DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
          factory.setConnectionFactory(connectionFactory);
          factory.setConcurrency("1-20");
          factory.setSessionTransacted(true); // Aktiviert lokale Transaktionen
         // factory.setTransactionManager(jmsTransactionManager);
          factory.setErrorHandler(errorHandler);
          return factory;
     }

     @Bean
     public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
          JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
          jmsTemplate.setSessionTransacted(true);
          return jmsTemplate;
     }

     @Bean(name = "jmsTransactionManager")
     public PlatformTransactionManager jmsTransactionManager(ConnectionFactory connectionFactory) {
          return new JmsTransactionManager(connectionFactory);
     }

     @Bean(name = "jmsTransactionTemplate")
     public TransactionTemplate transactionTemplate(@Qualifier("jmsTransactionManager") PlatformTransactionManager transactionManager) {
          return new TransactionTemplate(transactionManager);
     }

     private static final Logger logger = LoggerFactory.getLogger(JMSConfig.class);

     @Bean
     public ErrorHandler jmsErrorHandler() {
          return t -> {
               logger.error("Fehler im JMS Listener aufgetreten: ", t);
               // Optional: Weitere Fehlerbehandlungslogik
          };
     }

     */
}
