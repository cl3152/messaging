/*
package com.len.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication
public class MessagingIntegrationApplication implements CommandLineRunner {

	@Autowired
	private JmsTemplate jmsTemplate;

	public static void main(String[] args) {
		SpringApplication.run(MessagingIntegrationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		sendMessage("sourceQueue", "Testnachricht");
	}

	public void sendMessage(String destination, String message) {
		jmsTemplate.convertAndSend(destination, message);
		System.out.println("Nachricht gesendet: " + message);
	}
}
*/
