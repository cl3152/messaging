/*
package com.len.messaging.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.len.messaging.repository.TransferRepository;
import com.len.messaging.service.XmlDeserializationService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.QueueChannelSpec;
import org.springframework.integration.handler.DelayHandler;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;


@Configuration
public class AnnotationConfiguration {
    @Bean
    public QueueChannel delayChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel delayedChannel() {
        return new QueueChannel();
    }


    @Bean
    public QueueChannel directChannel() {
        return new QueueChannel();
    }

    @Bean
    public MessageChannel routerChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel processingChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel input() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel xmlInput() {
        return new DirectChannel();
    }

*/
/*    @Bean
    public MessageChannel toTransform() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel toLog() {
        return new DirectChannel();
    }*//*


    @Bean
    public MessageChannel discardChannel() {
        return new DirectChannel();
    }


    */
/* Enable if you want to use Java Configuration. Comment out the SimpleServiceActivator class *//*



*/
/*	@Bean
	@ServiceActivator(inputChannel = "toLog")
	public LoggingHandler logging() {
		LoggingHandler adapter = new LoggingHandler(LoggingHandler.Level.INFO);
		adapter.setLoggerName("SIMPLE_LOGGER");
		adapter.setLogExpressionString("headers.id + ': ' + payload");
		return adapter;
	}*//*


*/
/*    @Router(inputChannel = "routerChannel")
    public String route(String payload) {
        // Logik zur Entscheidung des Zielkanals
        if (payload.contains("Error")) {
            return "errorChannel";
        } else {
            return "processingChannel";
        }
    }*//*


    @Router(inputChannel = "routerChannel")
    public String route(Message<String> message) {
        if (message.getPayload().contains("Elster")) {
            return "delayChannel"; // Nachrichten werden an diesen Kanal weitergeleitet, wenn sie verzögert werden sollen
        } else {
            return "directChannel";
        }
    }

    @MessagingGateway
    public interface MyGateway {
        @Gateway(requestChannel = "routerChannel")
        void sendToRouter(String payload);
    }

    @Bean
    public SimpleMessageStore messageStore() {
        return new SimpleMessageStore();
    }

    @ServiceActivator(inputChannel = "delayChannel")
    @Bean
    public DelayHandler delayer() {
        DelayHandler handler = new DelayHandler("delayer.messageGroupId");
        handler.setDefaultDelay(3_000L);
        handler.setDelayExpressionString("headers['delay']");
        handler.setOutputChannelName("delayedChannel");
        return handler;
    }

    @ServiceActivator(inputChannel = "delayedChannel", poller = @Poller(fixedDelay = "5000"))
    public void processDelayedMessage(String message) {
        // Verarbeitung der verzögerten Nachrichten
        System.out.println("Delayed Processing: " + message);
    }

    @ServiceActivator(inputChannel = "directChannel")
    public void processDirectMessage(String message) {
        // Direkte Verarbeitung von Nachrichten
        System.out.println("Direct Processing: " + message);
    }

}

*/
/* Enable them for the Java Config example to work *//*



@MessageEndpoint
class SimpleFilter {
    */
/**
     * Standardmäßig verwirft Spring Integration solche Nachrichten stillschweigend, es sei denn,
     * es ist ein alternativer Ablauf oder eine Fehlerbehandlung konfiguriert.
     *//*

    @Filter(inputChannel = "input", outputChannel = "xmlInput", discardChannel = "discardChannel")
    public boolean process(String message) {

        return validateXML(message);
    }


    private boolean isValidXml(String xml) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(xml));
            return document != null; // Wenn das Dokument ohne Ausnahmen erstellt wird, ist es gültig
        } catch (DocumentException e) {
            return false; // Bei Parsing-Fehlern, nehmen wir an, dass es keine gültige XML ist
        }
    }

    public boolean validateXML(String xmlContent) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File("src/main/resources/schema.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlContent)));
            return true;  // XML ist gültig
        } catch (Exception e) {
            System.err.println("Validation error: " + e.getMessage());
            return false;  // XML ist ungültig
        }
    }

}

*/
/*@MessageEndpoint
class SimpleTransformer {

    @Transformer(inputChannel = "toTransform", outputChannel = "toLog")
    public String process(String message) {
        return "Hello ".concat(message);
    }
}*//*



@MessageEndpoint
class SimpleServiceActivator {

    private final XmlMapper xmlMapper;
    private final TransferRepository transferRepository;
    private final XmlDeserializationService xmlDeserializationService;

    SimpleServiceActivator(XmlMapper xmlMapper, TransferRepository transferRepository, XmlDeserializationService xmlDeserializationService) {
        this.xmlMapper = xmlMapper;
        this.transferRepository = transferRepository;
        this.xmlDeserializationService = xmlDeserializationService;
    }

    @ServiceActivator(inputChannel = "xmlInput")
    public void processXml(String content){
        try {
            xmlDeserializationService.deserializeXml(content);
        } catch (Exception e) {
            System.err.println("Fehler bei der Verarbeitung der Nachricht: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @ServiceActivator(inputChannel = "discardChannel")
    public void handleDiscardedMessage(String message) {
        System.out.println("Discarded: " + message);
    }

    @ServiceActivator(inputChannel = "processingChannel")
    public void processing(String message) {
        System.out.println("Processing: " + message);
    }


}*/
