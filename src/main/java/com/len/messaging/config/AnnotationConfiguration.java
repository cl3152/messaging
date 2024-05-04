package com.len.messaging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
public class AnnotationConfiguration {

    @Bean
    public MessageChannel input() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel toTransform() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel toLog() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel discardChannel() {
        return new DirectChannel();
    }


    /* Enable if you want to use Java Configuration. Comment out the SimpleServiceActivator class */


/*	@Bean
	@ServiceActivator(inputChannel = "toLog")
	public LoggingHandler logging() {
		LoggingHandler adapter = new LoggingHandler(LoggingHandler.Level.INFO);
		adapter.setLoggerName("SIMPLE_LOGGER");
		adapter.setLogExpressionString("headers.id + ': ' + payload");
		return adapter;
	}*/

}

/* Enable them for the Java Config example to work */


@MessageEndpoint
class SimpleFilter {
    /**Standardmäßig verwirft Spring Integration solche Nachrichten stillschweigend, es sei denn,
     * es ist ein alternativer Ablauf oder eine Fehlerbehandlung konfiguriert.*/
    @Filter(inputChannel = "input", outputChannel = "toTransform", discardChannel = "discardChannel")
    public boolean process(String message) {
        return "World".equals(message);
    }
}

@MessageEndpoint
class SimpleTransformer {

    @Transformer(inputChannel = "toTransform", outputChannel = "toLog")
    public String process(String message) {
        return "Hello ".concat(message);
    }
}


@MessageEndpoint
class SimpleServiceActivator {

    @ServiceActivator(inputChannel = "toLog")
    public void process(String message) {
        System.out.println("Processed: " + message);
    }

    @ServiceActivator(inputChannel = "discardChannel")
    public void handleDiscardedMessage(String message) {
        System.out.println("Discarded: " + message);
    }
}