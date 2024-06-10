package com.len.messaging.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
@EnableIntegration
@EnableConfigurationProperties(JMSProperties.class)
@EnableJms
public class IntegrationConfig2 {

    private final JMSProperties jmsProperties;
    private final ApplicationContext applicationContext;

    @Autowired
    public IntegrationConfig2(JMSProperties jmsProperties, ApplicationContext applicationContext) {
        this.jmsProperties = jmsProperties;
        this.applicationContext = applicationContext;
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(jmsProperties.getBrokerUrl());

        // Konfiguration der Redelivery-Policy
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);  // Anzahl der erneuten Zustellversuche auf 3 festlegen
        redeliveryPolicy.setInitialRedeliveryDelay(1000);  // Initiale Verzögerung vor der ersten erneuten Zustellung (in Millisekunden)
        redeliveryPolicy.setRedeliveryDelay(1000);  // Verzögerung zwischen den erneuten Zustellungen (in Millisekunden)

        factory.setRedeliveryPolicy(redeliveryPolicy);

        return new CachingConnectionFactory(factory);
    }

    @Bean
    @Qualifier("inputIntegration")
    public MessageChannel inputIntegration() {
        return new DirectChannel();
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-10");
        factory.setSessionTransacted(true);
        factory.setTransactionManager(jmsTransactionManager(connectionFactory()));
        factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        return factory;
    }


    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean(name = "jmsTransactionManager")
    public PlatformTransactionManager jmsTransactionManager(ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }
}
