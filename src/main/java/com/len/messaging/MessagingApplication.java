package com.len.messaging;

import com.len.messaging.config.JMSProperties;
import com.len.messaging.domain.Agvh;
import com.len.messaging.domain.Transfer;
import com.len.messaging.jms.SimpleSender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class MessagingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingApplication.class, args);
	}
	@Bean
	CommandLineRunner simple(JMSProperties props, SimpleSender sender, ObjectMapper objectMapper) {
		return args -> {
			// Erstellen eines Transfer-Objekts mit einer AgvhListe
			Transfer transfer = new Transfer();
			transfer.setNumber("12345");
			Agvh agvh = new Agvh();
			agvh.setText("Beispieltext");
			agvh.setTransfer(transfer);
			transfer.setAgvhList(Arrays.asList(agvh));

			// Konvertieren des Transfer-Objekts in ein JSON-String
			String jsonMessage = objectMapper.writeValueAsString(transfer);

			System.out.println(jsonMessage);

			// Senden der Nachricht
			sender.sendMessage(props.getQueue(), jsonMessage);
		};
	}

/*	@Bean
	CommandLineRunner simple(JMSProperties props, SimpleSender sender) {
		return args -> {
			sender.sendMessage(props.getQueue(), "World");
		};
	}*/
}