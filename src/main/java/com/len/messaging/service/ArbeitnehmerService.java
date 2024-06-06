package com.len.messaging.service;
import com.len.messaging.domain.Arbeitnehmer;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.domain.Transfer;
import com.len.messaging.exception.AussteuernException;
import com.len.messaging.repository.ArbeitnehmerRepository;
import com.len.messaging.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ArbeitnehmerService {

    private final TransferRepository transferRepository;
    private final ArbeitnehmerRepository arbeitnehmerRepository;

    public ArbeitnehmerService(TransferRepository transferRepository, ArbeitnehmerRepository arbeitnehmerRepository) {
        this.transferRepository = transferRepository;
        this.arbeitnehmerRepository = arbeitnehmerRepository;
    }

    /**
     * Falls auch JMS-Transaktionen verwendet werden sollen,
     * dann muss ein Transaktionsmanager angegeben werden:
     *
     * @Transactional("transactionManager")
     *
     * Implementorische Anlehnung an saveMappedTransfer aus ElsterIndexer
     */
    @Transactional
    public void processElsterDataForArbeitnehmer(ElsterData elsterData) throws AussteuernException {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        try {
            /* Im Original weicht die Verarbeitung hier im Detail ab.
            * Es handelt sich hier um eine vereinfachte Umsetzung.
            * */


            // Transfer schon in Datenbank vorhanden?

            // ja
            // Im Original noch eine "Mergelogik" und dann save


            // nein
            // dann save
            // falls erfolgreich save arbeitnehmer,
            // falls die Kombi Transfer + Arbeitnehmer noch nicht in DB vorhanden ist

            validateElsterData(elsterData);

            Transfer transfer = elsterData.getTransfer();
            Arbeitnehmer arbeitnehmer = elsterData.getArbeitnehmer();

            logger.info("ArbeitnehmerService versucht: ElsterData -> Transfer + Arbeitnehmer in DB");

            // Save the Transfer entity
            Transfer savedTransfer = transferRepository.save(transfer);

            // Save the Arbeitnehmer entity with the saved transfer ID
            arbeitnehmer.setTransferId(savedTransfer.getId());
            arbeitnehmerRepository.save(arbeitnehmer);

        }
        /*
         * Im Original werden folgende Exceptions geprüft, die zu einem Aussteuern führen:
         * IllegalStateException | RollbackException | SecurityException | HeuristicMixedException | HeuristicRollbackException |
         * javax.transaction.RollbackException | SystemException | NotSupportedException e
         * Was hier sinnvoll ist, muss überprüft werden.
         * */
        catch (DataAccessException | TransactionException e) {
            handleException("Datenbank- oder Transaktionsfehler bei der Verarbeitung von ElsterData aufgetreten", e);
        } catch (Exception e) {
            handleException("Bei der Verarbeitung von ElsterData ist ein unerwarteter Fehler aufgetreten", e);
        }
    }

    private void validateElsterData(ElsterData elsterData) throws AussteuernException {
        Optional.ofNullable(elsterData).orElseThrow(() -> new AussteuernException("ElsterData is null"));
        Optional.ofNullable(elsterData.getTransfer()).orElseThrow(() -> new AussteuernException("Transfer is null in ElsterData"));
        Optional.ofNullable(elsterData.getArbeitnehmer()).orElseThrow(() -> new AussteuernException("Arbeitnehmer is null in ElsterData"));
    }

    private void handleException(String message, Exception e) throws AussteuernException {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.error(message, e);
        throw new AussteuernException(message, e);
    }

}
