package com.len.messaging;

import com.len.messaging.config.JMSProperties;
import com.len.messaging.jms.SimpleSender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MessagingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingApplication.class, args);
	}

	// Es wird eine vereinfachte XML verwendet
	@Bean
	CommandLineRunner simple(JMSProperties props, SimpleSender sender) {
		return args -> {
			String xmlMessage = "<Elster>\n" +
					"    <Verfahren>ElsterLohn2</Verfahren>\n" +
					"    <DatenArt>AenderungslisteDUe</DatenArt>\n" +
					"    <Transfer>\n" +
					"        <Eingangts>1627848392</Eingangts>\n" +
					"        <Herstellerid>12345</Herstellerid>\n" +
					"        <Nutzdatenticket>ABC123</Nutzdatenticket>\n" +
					"        <Speichts>987654321</Speichts>\n" +
					"        <Transferticket>XYZ789</Transferticket>\n" +
					"    </Transfer>\n" +
					"    <Arbeitnehmer>\n" +
					"        <Abmeldedat>2021-08-01</Abmeldedat>\n" +
					"        <BB>ExampleBB</BB>\n" +
					"        <Gebdat>1980-01-01</Gebdat>\n" +
					"        <Gewuenschterfb>5000.00</Gewuenschterfb>\n" +
					"        <HAG>1.00</HAG>\n" +
					"        <Idnr>123456789</Idnr>\n" +
					"        <Refdatumag>2021-08-01</Refdatumag>\n" +
					"        <Speichts>987654321</Speichts>\n" +
					"        <Typ>ExampleTyp</Typ>\n" +
					"        <TransferId>12345</TransferId>\n" +
					"    </Arbeitnehmer>\n" +
					"</Elster>\n";

			sender.sendMessage(props.getQueue(), xmlMessage);
		};
	}
}