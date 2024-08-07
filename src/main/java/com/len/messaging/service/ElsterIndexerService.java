package com.len.messaging.service;

import com.len.messaging.domain.ElsterData;
import com.len.messaging.exception.AussteuernException;
import com.len.messaging.exception.SammellieferungException;
import com.len.messaging.util.xmlMapper.ElsterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ElsterIndexerService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ElsterMapper elsterMapper;
    private final ArbeitnehmerService arbeitnehmerService;

    public ElsterIndexerService(ElsterMapper elsterMapper, ArbeitnehmerService arbeitnehmerService) {
        this.elsterMapper = elsterMapper;
        this.arbeitnehmerService = arbeitnehmerService;
    }

    /**
     * Im Original werden mehrere Methoden aufgerufen.
     * Der Prozess ist hier nur verkürzt und vereinfacht umgesetzt.
     * <p>
     * Anlehnung an index- und map-Methode aus ElsterIndexer
     */
    public void indexAndMap(String xml) throws AussteuernException {
        /*
         * Einfaches Mapping.
         * Im Original viel komplexer:
         * Es wird mithilfe des ElsterRootElements (Ergebnis aus de.konsens.lavendel.srt.marshaller.ElsterMarshaller.unmarshal(xml))
         * und einer MapperFactory ein mapper erstellt.
         * Dieser enthält die entsprechenden Daten aus der xml - je nach Nutzdatenart
         * elsterData ist im Prototypen an diesen mapper angelehnt, da dieses Objekt die Daten enthält.
         */
        try {
            ElsterData elsterData = elsterMapper.convertXmlToElster(xml);
            if (elsterData != null) {
                logger.info("Konvertiertes Elsterdata: " + elsterData);
                /* Beispielhaftes Speichern von Daten (Vereinfachung).
                 * Im Original werden hier mehr Daten gespeichert.
                 *
                 * Im Original werden in der Methode folgende Exceptions geprüft, die zu einem Aussteuern führen:
                 * IllegalStateException | RollbackException | SecurityException | HeuristicMixedException | HeuristicRollbackException |
                 * javax.transaction.RollbackException | SystemException | NotSupportedException e
                 * Was hier sinnvoll ist, muss überprüft werden.
                 * Evtl. PersistenceExceptionTranslator einsetzen
                 * */
                arbeitnehmerService.processElsterDataForArbeitnehmer(elsterData);
            } else {
                // Wenn Mapping nicht gelingt
                throw new AussteuernException("elsterData == null");
            }
        } catch (SammellieferungException e) {
            /* Im Original wird hier, falls das Mapping mit der MapperFactory nicht klappt,
            eine Sammellieferungexception gefangen und behandelt.
             */
        } finally {
            logger.info("indexAndMap-Methode im ElsterIndexer beendet.");
        }

    }

}