package com.len.messaging.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.jms.DefaultJmsHeaderMapper;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.jms.dsl.JmsMessageDrivenChannelAdapterSpec;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageChannel;

import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
@EnableIntegration
@EnableConfigurationProperties(JMSProperties.class)
@EnableJms
public class IntegrationConfig {

    private final JMSProperties jmsProperties;

    public IntegrationConfig(JMSProperties jmsProperties) {
        this.jmsProperties = jmsProperties;
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(jmsProperties.getBrokerUrl());
        //factory.setUserName(jmsProperties.getUser());
        //factory.setPassword(jmsProperties.getPassword());

        // Konfiguration der Redelivery-Policy
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);  // Anzahl der erneuten Zustellversuche auf 3 festlegen
        redeliveryPolicy.setInitialRedeliveryDelay(1000);  // Initiale Verzögerung vor der ersten erneuten Zustellung (in Millisekunden)
        redeliveryPolicy.setRedeliveryDelay(1000);  // Verzögerung zwischen den erneuten Zustellungen (in Millisekunden)

        factory.setRedeliveryPolicy(redeliveryPolicy);

        return new CachingConnectionFactory(factory);    }

    @Bean
    public MessageChannel inputIntegration() {
        return new DirectChannel();
    }

    @Bean
    public DefaultMessageListenerContainer messageListenerContainer() {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setDestinationName(jmsProperties.getQueue());
        container.setTransactionManager(jmsTransactionManager(connectionFactory()));
        // default auto bleibt glaub ich:
        // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/listener/DefaultMessageListenerContainer.html
        //
        container.setSessionTransacted(true);
        container.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        // container.setErrorHandler(jmsErrorHandler()); Kann mit Spring Integration nicht verwendet werden.
        // https://docs.spring.io/spring-integration/reference/changes-4.1-4.2.html#conversion-errors-in-message-driven-endpoints transacted ist default
        // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/listener/AbstractMessageListenerContainer.html
        // mMn gleiches Verhalten bei Auto Acknowledge
        return container;
    }

    @Bean
    public JmsMessageDrivenChannelAdapterSpec<?> jmsMessageDrivenChannelAdapter() {
        return Jms.messageDrivenChannelAdapter(messageListenerContainer())
                .headerMapper(new DefaultJmsHeaderMapper())
                .outputChannel(inputIntegration());
    }


  //Da nichts in eine Queue gesendet wird, wird es momentan nicht verwendet.

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }




  //Es werden keine JMS-Transaktionen verwendet, deswegen auskommentiert

    @Bean(name = "jmsTransactionManager")
    public PlatformTransactionManager jmsTransactionManager(ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }


    /*
     *//**
     * Bei Verwendung von Spring-Integration und einem Delay-Handler (= nicht ausschließelich DirectChannels)
     * muss eine asynchrone Fehlerbehandlung verwendet werden.
     * https://docs.spring.io/spring-integration/reference/error-handling.html
     * Das ist bisher nicht implementiert.
     * Dieser ErrorHandler kann nicht verwendet werden.
     *//*
    @Bean
    public ErrorHandler jmsErrorHandler() {
        return new ErrorHandler() {
            private final Logger logger = LoggerFactory.getLogger("JmsErrorHandler");

            @Override
            public void handleError(Throwable t) {
                // hier sollte momentan nichts ankommen, da alles in die Datenbank ausgesteuert wird
                // Mit Delayhandler sowieso nicht verwendbar.
                logger.error("Fehler im JMS Listener aufgetreten: ", t);

            }
        };
    }*/

}
