package com.len.messaging.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.len.messaging.domain.ElsterData;
import com.len.messaging.exception.AussteuernException;
import com.len.messaging.util.xmlMapper.ElsterMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElsterIndexerService {

    private final ElsterMapper elsterMapper;
    private final ArbeitnehmerService arbeitnehmerService;

    public ElsterIndexerService(ElsterMapper elsterMapper, ArbeitnehmerService arbeitnehmerService) {
        this.elsterMapper = elsterMapper;
        this.arbeitnehmerService = arbeitnehmerService;
    }

    /**
     * Im Original werden mehrere Methoden aufgerufen.
     * Der Prozess ist hier nur verkürzt und vereinfacht umgesetzt.
     *
     * Anlehnung an map-Methode aus ElsterIndexer
     * */
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
                System.out.println("Konvertiertes Elsterdata: " + elsterData);
                /* Beispielhaftes Speichern von Daten (Vereinfachung).
                 * Im Original werden hier mehr Daten gespeichert.
                 */
                arbeitnehmerService.processElsterDataForArbeitnehmer(elsterData);
            } else {
                throw new AussteuernException("elsterData = null");
            }
        } catch (Exception e){
            /* Im Original wird hier, falls das Mapping mit der MapperFactory nicht klappt,
            eine Sammellieferungexception gefangen und behandelt.
             */
        }

    }

}