package com.len.messaging.config;

import com.len.messaging.jms.MessageEvaluator;
import com.len.messaging.service.IndexerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.DelayHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class MessagingConfig {

    @Bean
    public MessageChannel inputIntegration() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel delayingChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel processingChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel transformingChannel() { return new DirectChannel(); }

    private final MessageEvaluator messageEvaluator;

    private final ProcessingGateway processingGateway;

    private final IndexerService indexerService;

    private static final Logger logger = LoggerFactory.getLogger(MessagingConfig.class);

    public MessagingConfig(MessageEvaluator messageEvaluator, ProcessingGateway processingGateway, IndexerService indexerService) {
        this.messageEvaluator = messageEvaluator;
        this.processingGateway = processingGateway;
        this.indexerService = indexerService;
    }

    @MessagingGateway
    public interface ProcessingGateway {

        @Gateway(requestChannel = "errorChannel")
        void sendToErrorChannel(Message<String> message);

        @Gateway(requestChannel = "inputIntegration")
        void sendToInputIntegration(Message<String> message);

        @Gateway(requestChannel = "processingChannel")
        void sendToProcessingChannel(Message<String> message);

        @Gateway(requestChannel = "transformingChannel")
        void sendToTransformingChannel(Message<String> message);

    }

    @ServiceActivator(inputChannel = "inputIntegration")
    public void receivingInput(Message<String> message) {
        String payload = message.getPayload();
        MessageEvaluator.Action action = messageEvaluator.evaluate(payload);
        // Filter Logik: Aussortieren, Verzögern, Direkt Verarbeiten
        switch (action) {
            case DIREKT_VERARBEITEN:
                processingGateway.sendToProcessingChannel(message);
                break;
            case KEIN_XML, NO_DATA, AUSSTEUERN:
                processingGateway.sendToErrorChannel(message);
                break;
            case KURZ_VERZOEGERN:
                logger.info("Case: kurz verzögern");
            case LANG_VERZOEGERN:
                logger.info("Case: lang verzögern");
            case MITTEL_VERZOEGERN:
                logger.info("Case: mittel verzögern");
                processingGateway.sendToTransformingChannel(message);
                break;
            default:
                logger.info("Case: default");
                break;
        }
    }

    @Transformer(inputChannel = "transformingChannel", outputChannel = "delayingChannel")
    public Message<String> transformingInput(Message<String> message) {
        logger.info("Eine Verzögerung wird im Headerfeld delay hinzugefuegt.");
        MessageEvaluator.Action action = messageEvaluator.evaluate(message.getPayload());
        long delay = action.getDelayFactor() * 2000;

        return MessageBuilder.fromMessage(message)
                .setHeader("delay", delay)
                .build();
    };
    @Bean
    @ServiceActivator(inputChannel = "delayingChannel")
    public DelayHandler delayer() {
        DelayHandler handler = new DelayHandler("delayer.messageGroupId");
        handler.setDelayExpressionString("headers['delay']");
        handler.setDefaultDelay(0L);
        handler.setOutputChannelName("processingChannel");
        return handler;
    }

    @ServiceActivator(inputChannel = "processingChannel")
    public void processingInput(Message<String> message) {
        logger.info("Input wird verarbeitet.");
        /*
        * Im Original wird hier noch eine Methode process ausgeführt, die die Multithreading-Verarbeitung
        * kontrolliert.
         */
        indexerService.mapAndStore(message.getPayload());
    }

    @ServiceActivator(inputChannel = "errorChannel")
    public void handleError(Message<String> message) {
        String error = message.getPayload();
        logger.error("Error bei der Nachrichtenverarbeitung: {}", error);
    }

}
