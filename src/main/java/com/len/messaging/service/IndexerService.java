
package com.len.messaging.service;

import com.len.messaging.domain.Fehler;
import com.len.messaging.exception.AussteuernException;
import com.len.messaging.repository.FehlerRepository;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


import static com.len.messaging.util.ExceptionUtil.contains;

@Service
public class IndexerService {

    private final ElsterIndexerService elsterIndexerService;

    private final FehlerRepository fehlerRepository;

    public IndexerService(ElsterIndexerService elsterIndexerService, FehlerRepository fehlerRepository) {
        this.elsterIndexerService = elsterIndexerService;
        this.fehlerRepository = fehlerRepository;
    }

    public void mapAndStore(String xml) {

        /* Im Original findet hier u. a. die Validierung der XML gegen ein XSD-Schema statt
        * ElsterRootElement elster = de.konsens.lavendel.srt.marshaller.ElsterMarshaller.unmarshal(xml);
        * Dieser Schritt wird im Prototypen übersprungen.
        *
        * Weiter Logik im Original: u. a. prüfen auf Testmerker
        */

         try {
            elsterIndexerService.indexAndMap(xml);
            // throw new RuntimeException("test");

        } catch (AussteuernException e) {
            fehlerAussteuern(xml, e);
        } catch (Exception e) {
             if(true){
            //if (retrySinnvoll(e)) {
                /* führt automatisch zu einem Redelivery
                * das Rollback muss nicht manuell angestoßen werden
                * (im Gegenteil zum Original, da wird in der übergeordneten onMessage Fn diese gefangen
                * und ein manuelles Rollback initiiert.
                */
                throw new RetryException("Redelivery sinnvoll:", e);
            } else {
                // TODO: alle anderen Exceptions auch aussteuern? bisher so!
                // kann nicht in Errorhandler ausgelagert werden, wenn die xml beibehalten werden soll.
                fehlerAussteuern(xml, e);
            }

        }
    }

    private void fehlerAussteuern(String xml, Exception e) {
        System.out.println("aussteuern");
        String errorMessage = "Nicht parsbare Daten";

        // Sehr gekürzte Protokollierung der Fehler - nur zu Demonstrationszwecken
        Fehler fehler = new Fehler()
                .message(errorMessage);
          //      .stacktrace(e)
           //     .rohdaten(xml);

        fehlerRepository.save(fehler);
    }




    private static boolean retrySinnvoll(Exception exception) {
        return istUniqueKeyViolationException(exception);
    }



    private static boolean istUniqueKeyViolationException(Exception exception) {
        return contains(exception, "TRANSFER_TT_NDT_IDX");
    }


}