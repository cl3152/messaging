package com.len.messaging.config;

import com.len.messaging.domain.Arbeitnehmer;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.domain.Transfer;
import com.len.messaging.jms.MessageEvaluator;
import com.len.messaging.repository.ArbeitnehmerRepository;
import com.len.messaging.repository.TransferRepository;
import com.len.messaging.service.ArbeitnehmerService;
import com.len.messaging.util.xmlMapper.ElsterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.DelayHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;

import java.io.IOException;

@Configuration
@EnableIntegration
public class MessagingConfig {

    private final TransferRepository transferRepository;
    private final ArbeitnehmerRepository arbeitnehmerRepository;
    private final ElsterMapper elsterMapper;

    private final ArbeitnehmerService arbeitnehmerService;



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

    public MessagingConfig(TransferRepository transferRepository, ArbeitnehmerRepository arbeitnehmerRepository, ElsterMapper elsterMapper, ArbeitnehmerService arbeitnehmerService, MessageEvaluator messageEvaluator, ProcessingGateway processingGateway) {
        this.transferRepository = transferRepository;
        this.arbeitnehmerRepository = arbeitnehmerRepository;
        this.elsterMapper = elsterMapper;
        this.arbeitnehmerService = arbeitnehmerService;
        this.messageEvaluator = messageEvaluator;
        this.processingGateway = processingGateway;
    }

    private static final Logger logger = LoggerFactory.getLogger(MessagingConfig.class);


    @MessagingGateway
    public interface ProcessingGateway {

        @Gateway(requestChannel = "errorChannel")
        void sendToErrorChannel(Message<String> message);

        @Gateway(requestChannel = "inputChannel")
        void sendToRouter(Message<String> message);

        @Gateway(requestChannel = "processingChannel")
        void sendToProcessingChannel(Message<String> message);

        @Gateway(requestChannel = "delayingChannel")
        void sendToDelayingChannel(Message<String> message);

        @Gateway(requestChannel = "transformingChannel")
        void sendToTransformingChannel(Message<String> message);

    }

    @Bean
    @ServiceActivator(inputChannel = "delayingChannel")
    public DelayHandler delayer() {
        DelayHandler handler = new DelayHandler("delayer.messageGroupId");
        handler.setDelayExpressionString("headers['delay']");
        handler.setDefaultDelay(0L);
        handler.setOutputChannelName("processingChannel");
        return handler;
    }

@Transformer(inputChannel = "transformingChannel", outputChannel = "delayingChannel")
public Message<String> transformingInput(Message<String> message) {
        logger.info("Transforming messaging - adding Delay in header");
        MessageEvaluator.Action action = messageEvaluator.evaluate(message.getPayload());
        long delay = action.getDelayFactor() * 2000;

    return MessageBuilder.fromMessage(message)
                .setHeader("delay", delay)
                .build();
    };

    @ServiceActivator(inputChannel = "inputChannel")
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
                System.out.println("kurz verzögern: ");
            case LANG_VERZOEGERN:
                System.out.println("lang verzögern: ");
            case MITTEL_VERZOEGERN:
                System.out.println("mittel verzögern: ");
                processingGateway.sendToTransformingChannel(message);
                break;
            default:
                System.out.println("Default: " + message);
                break;
        }
    }

    @ServiceActivator(inputChannel = "processingChannel")
    public void processingInput(Message<String> message) throws IOException {
        logger.info("Processing message has this header: {}", message.getHeaders());
        logger.info(message.getPayload());
        System.out.println("Input Processing. ");
        // XML Validierung?!
        //
        // Mapper -> Objekte aus XML bekommen - Diese Logik in in Produktiv komplizierter.
         ElsterData elsterData = elsterMapper.convertXmlToElster(message.getPayload());
        System.out.println("PROCESSING Elsterdata: " + elsterData);

        // Je nachdem was in elsterData drin ist unterschiedliche Services?!
        arbeitnehmerService.processElsterDataForArbeitnehmer(elsterData);
    }


    @ServiceActivator(inputChannel = "errorChannel")
    public void handleError(Message<String> message) {
        String error = message.getPayload();
        logger.error("Error processing message: {}", error);
        // Senden ans Monitoring?!
    }





}
