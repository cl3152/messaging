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
     * @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
     * (in JpaConfig dann definiert)
     *
     * Anlehnung an die saveMappedTransfer-Methode aus ElsterIndexer
     */
    @Transactional(rollbackFor = Exception.class)
    public void processElsterDataForArbeitnehmer(ElsterData elsterData) throws AussteuernException {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        try {
            /* Im Original weicht die Verarbeitung hier im Detail ab.
            * z. B. wird erst überprüft, ob ein Transfer schon in der DB enthalten ist.
            * Es handelt sich hier um eine vereinfachte Umsetzung.
            * */
            validateElsterData(elsterData);

            Transfer transfer = elsterData.getTransfer();
            Arbeitnehmer arbeitnehmer = elsterData.getArbeitnehmer();

            Transfer savedTransfer = transferRepository.save(transfer);

            /* Zum Testen:
             throw new RuntimeException("Error simulieren: Transaktion nicht erfolgreich");
             */

            arbeitnehmer.setTransferId(savedTransfer.getId());
            arbeitnehmerRepository.save(arbeitnehmer);

            logger.info("ArbeitnehmerService hat ElsterData=Transfer + Arbeitnehmer in DB gespeichert");
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
            // Dieser Catch-Block ist im Original nicht enthalten.
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
