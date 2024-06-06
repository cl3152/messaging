package com.len.messaging.config;

import com.len.messaging.exception.AussteuernException;
import jakarta.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.jms.DefaultJmsHeaderMapper;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.integration.jms.dsl.JmsMessageDrivenChannelAdapterSpec;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import jakarta.jms.Session;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.RetryException;
import org.springframework.util.ErrorHandler;

@Configuration
@EnableIntegration
@EnableConfigurationProperties(JMSProperties.class)
@EnableJms
public class IntegrationConfig {

    private final JMSProperties jmsProperties;

    public IntegrationConfig(JMSProperties jmsProperties) {
        this.jmsProperties = jmsProperties;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Konfiguration der Redelivery-Policy
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);  // Anzahl der erneuten Zustellversuche auf 3 festlegen
        redeliveryPolicy.setInitialRedeliveryDelay(1000);  // Initiale Verzögerung vor der ersten erneuten Zustellung (in Millisekunden)
        redeliveryPolicy.setRedeliveryDelay(1000);  // Verzögerung zwischen den erneuten Zustellungen (in Millisekunden)

        factory.setRedeliveryPolicy(redeliveryPolicy);
        return factory;
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
    public DefaultMessageListenerContainer messageListenerContainer() {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setDestinationName(jmsProperties.getQueue());
        container.setSessionTransacted(false);
        container.setErrorHandler(jmsErrorHandler());
        // https://docs.spring.io/spring-integration/reference/changes-4.1-4.2.html#conversion-errors-in-message-driven-endpoints transacted ist default
        // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/listener/AbstractMessageListenerContainer.html
        // mMn gleiches Verhalten bei Auto Acknowledge
        container.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return container;
    }
    /**
     * Bei Verwendung von Spring-Integration und einem Delay-Handler (= nicht ausschließelich DirectChannels)
     * muss eine asynchrone Fehlerbehandlung verwendet werden.
     * https://docs.spring.io/spring-integration/reference/error-handling.html
     * Das ist bisher nicht implementiert.
     */
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
    }

    @Bean
    public JmsMessageDrivenChannelAdapterSpec<?> jmsMessageDrivenChannelAdapter() {
        return Jms.messageDrivenChannelAdapter(messageListenerContainer())
                .headerMapper(new DefaultJmsHeaderMapper())
                .outputChannel(inputIntegration())
                ;
    }

}
