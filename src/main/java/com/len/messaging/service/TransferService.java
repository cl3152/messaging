package com.len.messaging.service;

import com.len.messaging.domain.Agvh;
import com.len.messaging.domain.Transfer;
import com.len.messaging.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

    @Transactional
    public Transfer createTransferWithAgvh(Transfer transfer, List<Agvh> agvhList) {
        for (Agvh agvh : agvhList) {
            agvh.setTransfer(transfer);  // Setze den Transfer für jedes Agvh
        }
        transfer.setAgvhList(agvhList);  // Füge die Agvh Liste zum Transfer hinzu
        return transferRepository.save(transfer);  // Speichert den Transfer und alle Agvh
    }
}
