package com.len.messaging.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
// Konfiguration mithilfe des Adapters - wird nicht empfohlen
// @Configuration
@EnableIntegration
@EnableConfigurationProperties(JMSProperties.class)
@EnableJms
public class IntegrationConfigAdapter {

    private final JMSProperties jmsProperties;

    public IntegrationConfigAdapter(JMSProperties jmsProperties) {
        this.jmsProperties = jmsProperties;
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();

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
        // Setzen des Bestaetigungsmodus klappt nicht
        container.setSessionTransacted(true);
        container.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        return container;
    }

    @Bean
    public JmsMessageDrivenChannelAdapterSpec<?> jmsMessageDrivenChannelAdapter() {
        return Jms.messageDrivenChannelAdapter(messageListenerContainer())
                .headerMapper(new DefaultJmsHeaderMapper())
                .outputChannel(inputIntegration());
    }

}
