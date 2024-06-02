/*
package com.len.messaging.service;


import com.len.messaging.util.ElsterMarshaller;
import com.len.messaging.util.ElsterUtil;
import com.len.messaging.util.xml.ElsterRootElement;
import org.springframework.beans.factory.annotation.Autowired;

// alt: IndexerBean
public class IndexerService {

    private ElsterMarshaller marshaller = new ElsterMarshaller();

    @Autowired
    private ElsterIndexerService elsterIndexer;

    public void mapAndStore(String xml, long speichts) {

        try {
            ElsterRootElement elster = marshaller.unmarshal(xml);
            //warnAboutOldElsterVersion(elster);
            elsterIndexer.index(elster, speichts);


        } catch (Exception e) {
*/
/*            if (retrySinnvoll(e)) {
                throw new RetryException(e);
            } else {
                fehlerAussteuern(xml, speichts, e, ThreadLogDaten.mapperTyp());
            }*//*

        } finally {
*/
/*            long ende = System.currentTimeMillis();
            long dauer = ende - start;
            String msgFormat = "Verarbeitung speichts=%s art=\"%s\" in \"%s\"ms beendet.";
            logger.infof(msgFormat, speichts, ThreadLogDaten.mapperTyp(), dauer);*//*

        }
    }

}
*/
