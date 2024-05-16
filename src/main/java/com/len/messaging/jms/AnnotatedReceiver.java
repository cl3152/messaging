/*
package com.len.messaging.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.len.messaging.config.AnnotationConfiguration;
import com.len.messaging.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedReceiver {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private XmlMapper xmlMapper;

    @Autowired
    private TransferRepository transferRepository;

    private final MessageChannel input;

    @Autowired
    private AnnotationConfiguration.MyGateway gateway;

    public AnnotatedReceiver(MessageChannel input) {
        this.input = input;
    }

    @JmsListener(destination = "${apress.jms.queue}")
    public void processMessage(String content) {
        gateway.sendToRouter(content);
        //input.send(MessageBuilder.withPayload(content).build());
*/
/*        try {
            Transfer transfer = xmlMapper.readValue(content, Transfer.class);
            //Transfer transfer = objectMapper.readValue(content, Transfer.class);
            transfer = transferRepository.save(transfer); // Speichert den Transfer und erhält eine generierte ID

            if (transfer.getAgvhList() != null) {
                for (Agvh agvh : transfer.getAgvhList()) {
                    agvh.setTransfer(transfer); // Setzt den Transfer für jedes Agvh
                    agvhRepository.save(agvh); // Speichert jedes Agvh
                }
            }

            System.out.println("Transfer und zugehörige Agvh gespeichert.");
        } catch (Exception e) {
            System.err.println("Fehler bei der Verarbeitung der Nachricht: " + e.getMessage());
            e.printStackTrace();
        }*//*


    }

*/
/*    @JmsListener(destination = "${apress.jms.queue}")
    public void processMessage(String content) {

        System.out.println("JMSSender: " + content);
        input.send(MessageBuilder.withPayload(content).build());

    }*//*


}*/
