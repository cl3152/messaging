/*
package com.len.messaging.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.len.messaging.domain.Arbeitnehmer;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.domain.Transfer;
import com.len.messaging.repository.ArbeitnehmerRepository;
import com.len.messaging.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class XmlDeserializationService {

    private XmlMapper xmlMapper;
    private TransferRepository transferRepository;
    private ArbeitnehmerRepository arbeitnehmerRepository;

    public XmlDeserializationService(XmlMapper xmlMapper, TransferRepository transferRepository, ArbeitnehmerRepository arbeitnehmerRepository) {
        this.xmlMapper = xmlMapper;
        this.transferRepository = transferRepository;
        this.arbeitnehmerRepository = arbeitnehmerRepository;
    }

    @Transactional
    public void deserializeXml(String xmlContent) throws IOException {
        ElsterData elsterData = xmlMapper.readValue(xmlContent, ElsterData.class);
        Transfer transfer = new Transfer();
        transfer = elsterData.getTransfer();
        Arbeitnehmer arbeitnehmer = new Arbeitnehmer();
        arbeitnehmer = elsterData.getArbeitnehmer();
        arbeitnehmer.setTransfer(transfer);

        transferRepository.save(transfer);
        arbeitnehmerRepository.save(arbeitnehmer);
        System.out.println("Transfer und Arbeitnehmer gespeichert");

    }

}
*/
