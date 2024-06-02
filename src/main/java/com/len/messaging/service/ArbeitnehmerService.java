package com.len.messaging.service;

import com.len.messaging.domain.Arbeitnehmer;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.domain.Transfer;
import com.len.messaging.repository.ArbeitnehmerRepository;
import com.len.messaging.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArbeitnehmerService {

    private final TransferRepository transferRepository;
    private final ArbeitnehmerRepository arbeitnehmerRepository;

    public ArbeitnehmerService(TransferRepository transferRepository, ArbeitnehmerRepository arbeitnehmerRepository) {
        this.transferRepository = transferRepository;
        this.arbeitnehmerRepository = arbeitnehmerRepository;
    }

    @Transactional("transactionManager")
    public void processElsterDataForArbeitnehmer(ElsterData elsterData) {
        System.out.println("ElsterData -> Arbeitnehmer");

        // Save the Transfer entity
        Transfer transfer = elsterData.getTransfer();
        Transfer savedTransfer = transferRepository.save(transfer);

        // Save the Arbeitnehmer entity with the saved transfer ID
        Arbeitnehmer arbeitnehmer = elsterData.getArbeitnehmer();
        arbeitnehmer.setTransferId(savedTransfer.getId());

        arbeitnehmerRepository.save(arbeitnehmer);
    }

}
