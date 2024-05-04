package com.len.messaging.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.len.messaging.domain.Agvh;
import com.len.messaging.domain.Transfer;
import com.len.messaging.repository.AgvhRepository;
import com.len.messaging.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedReceiver {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private AgvhRepository agvhRepository;

    private final MessageChannel input;

    public AnnotatedReceiver(MessageChannel input) {
        this.input = input;
    }

    @JmsListener(destination = "${apress.jms.queue}")
    public void processMessage(String content) {
        try {
            Transfer transfer = objectMapper.readValue(content, Transfer.class);
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
        }

    }

/*    @JmsListener(destination = "${apress.jms.queue}")
    public void processMessage(String content) {

        System.out.println("JMSSender: " + content);
        input.send(MessageBuilder.withPayload(content).build());

    }*/

}