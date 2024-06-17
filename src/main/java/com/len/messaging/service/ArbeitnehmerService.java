package com.len.messaging.service;

import com.len.messaging.domain.Arbeitnehmer;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.domain.Transfer;
import com.len.messaging.exception.AussteuernException;
import com.len.messaging.repository.ArbeitnehmerRepository;
import com.len.messaging.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Service
public class ArbeitnehmerService {

    private final TransferRepository transferRepository;
    private final ArbeitnehmerRepository arbeitnehmerRepository;
    private final TransactionTemplate transactionTemplate;

    public ArbeitnehmerService(TransferRepository transferRepository, ArbeitnehmerRepository arbeitnehmerRepository, TransactionTemplate transactionTemplate) {
        this.transferRepository = transferRepository;
        this.arbeitnehmerRepository = arbeitnehmerRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void processElsterDataForArbeitnehmer(@NonNull ElsterData elsterData) throws AussteuernException {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        try {
            validateElsterData(elsterData);

            Transfer transfer = elsterData.getTransfer();
            Arbeitnehmer arbeitnehmer = elsterData.getArbeitnehmer();

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                    try {
                        Optional<Transfer> existingTransferOpt = transferRepository.findByTransferticketAndNutzdatenticket(
                                transfer.getTransferticket(), transfer.getNutzdatenticket());

                        if (existingTransferOpt.isEmpty()) {
                            Transfer savedTransfer = transferRepository.save(transfer);
                            logger.info("Transfer gespeichert");

                            Optional<Arbeitnehmer> existingArbeitnehmerOpt = arbeitnehmerRepository.findSingle(
                                    savedTransfer.getId(), arbeitnehmer.getIdnr(), arbeitnehmer.getTyp());

                            if (existingArbeitnehmerOpt.isEmpty()) {
                                arbeitnehmer.setTransferId(savedTransfer.getId());
                                arbeitnehmerRepository.save(arbeitnehmer);
                                logger.info("Arbeitnehmer zum neuen Transfer gespeichert");
                            } else {
                                logger.info("Den Arbeitnehmer (idnr) zu dieser TransferId und diesem Typen gibt es schon.");
                            }

                        } else {
                            logger.info("Es gibt schon einen Transfer-DB-Eintrag");
                            Transfer existingTransfer = existingTransferOpt.get();
                            Optional<Arbeitnehmer> existingArbeitnehmerOpt = arbeitnehmerRepository.findSingle(
                                    existingTransfer.getId(), arbeitnehmer.getIdnr(), arbeitnehmer.getTyp());

                            if (existingArbeitnehmerOpt.isEmpty()) {
                                arbeitnehmer.setTransferId(existingTransfer.getId());
                                arbeitnehmerRepository.save(arbeitnehmer);
                                logger.info("Arbeitnehmer zum existierenden Transfer gespeichert");
                            } else {
                                logger.info("Den Arbeitnehmer (idnr) zu dieser TransferId und diesem Typen gibt es schon.");
                            }
                        }

                        throw new RuntimeException("Test");
                    } catch (Exception ex) {
                        status.setRollbackOnly();
                        //throw ex;
                    }
                }
            });
        } catch (DataAccessException | TransactionException e) {
            handleException("Datenbank- oder Transaktionsfehler bei der Verarbeitung von ElsterData aufgetreten", e);
        } catch (Exception e) {
            handleException("Bei der Verarbeitung von ElsterData ist ein unerwarteter Fehler aufgetreten", e);
        }
    }

    private void validateElsterData(ElsterData elsterData) throws AussteuernException {
        Optional.ofNullable(elsterData.getTransfer()).orElseThrow(() -> new AussteuernException("Transfer is null in ElsterData"));
        Optional.ofNullable(elsterData.getArbeitnehmer()).orElseThrow(() -> new AussteuernException("Arbeitnehmer is null in ElsterData"));
    }

    private void handleException(String message, Exception e) throws AussteuernException {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.error(message, e);
        throw new AussteuernException(message, e);
    }
}
