package com.len.messaging.service;

import com.len.messaging.domain.Arbeitnehmer;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.domain.Transfer;
import com.len.messaging.exception.AussteuernException;
import com.len.messaging.repository.ArbeitnehmerRepository;
import com.len.messaging.repository.TransferRepository;
//import de.konsens.lavendel.srt.logging.SrtLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class ArbeitnehmerService {

    // Abhaengigkeiten muessen zuerst hinzugefuegt werden
    // private final SrtLogger srtLogger;

    private final TransferRepository transferRepository;
    private final ArbeitnehmerRepository arbeitnehmerRepository;

    public ArbeitnehmerService(
        //SrtLogger srtLogger,
        TransferRepository transferRepository, ArbeitnehmerRepository arbeitnehmerRepository) {
        //this.srtLogger = srtLogger;
        this.transferRepository = transferRepository;
        this.arbeitnehmerRepository = arbeitnehmerRepository;
    }

    /**
     * Falls ein seperater JMS-Transaktionsmanager verwendet werden soll,
     * dann muss ein Transaktionsmanager angegeben werden:
     *
     * @Transactional(transactionManager = "transactionManager")
     * (in JpaConfig dann definiert)
     *
     * Anlehnung an die saveMappedTransfer-Methode aus ElsterIndexer
     */
    @Transactional
    public void processElsterDataForArbeitnehmer(ElsterData elsterData) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        // srtLogger.nutzdatenticket(elsterData.getTransfer().getNutzdatenticket());
        // srtLogger.error().msg("Test des SRT-Loggers - klappt :)").flush();

         /* Im Original weicht die Verarbeitung hier im Detail ab.
            * z. B. wird, falls der Transfer schon in der DB enthalten ist, der Transfer "gemerged".
            * Es handelt sich hier um eine vereinfachte Umsetzung.
            * */
            validateElsterData(elsterData);

            Transfer transfer = elsterData.getTransfer();
            Arbeitnehmer arbeitnehmer = elsterData.getArbeitnehmer();

            Optional<Transfer> existingTransferOpt = transferRepository.findByTransferticketAndNutzdatenticket(
                    transfer.getTransferticket(), transfer.getNutzdatenticket());

            if (existingTransferOpt.isEmpty()){
                Transfer savedTransfer = transferRepository.save(transfer);
                logger.info("Neuer Transfer gespeichert");

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
    }

    private void validateElsterData(ElsterData elsterData) throws AussteuernException {
        Optional.ofNullable(elsterData.getTransfer()).orElseThrow(() -> new AussteuernException("Transfer is null in ElsterData"));
        Optional.ofNullable(elsterData.getArbeitnehmer()).orElseThrow(() -> new AussteuernException("Arbeitnehmer is null in ElsterData"));
    }


}
