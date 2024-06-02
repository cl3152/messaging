/*
package com.len.messaging.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.input.ReaderInputStream;
import org.xml.sax.SAXException;

import de.elster._2002.xmlschema.Elster;
import de.konsens.lavendel.srt.logging.SrtLogger;
import de.konsens.lavendel.srt.xml.ElsterRootElement;

public class ElsterMarshaller {

    public static final String XSD_ELSTER_TH9  = "/srt/elster0810_elo2_extern-2015.xsd";
    public static final String XSD_ELSTER_TH11 = "/srt/srt-th9ndh11.xsd";

    private Unmarshaller unmarshaller;
    private Marshaller marshaller;
    private ValidationEventCollector validationEventCollector = new ValidationEventCollector();

    private List<Charset> charsetsToTry = new ArrayList<>();

    static {
        // Vermeide Cloadloader-Problem
        // https://stackoverflow.com/questions/50237516/proper-fix-for-java-10-complaining-about-illegal-reflection-access-by-jaxb-impl
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "");
    }

    public synchronized void init() throws SAXException, JAXBException {
        if (unmarshaller == null || marshaller == null) {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // vgl. SchemaFactory.newSchema(URL)
            Source schemaTH9  = new StreamSource(getClass().getResource(XSD_ELSTER_TH9).toExternalForm());
            Source schemaTH11 = new StreamSource(getClass().getResource(XSD_ELSTER_TH11).toExternalForm());

            Schema schema = sf.newSchema(new Source[]{schemaTH9, schemaTH11});
            JAXBContext jc = JAXBContext.newInstance(
                    Elster.class,
                    de.elster.elsterxml.schema.v11.Elster.class
            );

            unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(validationEventCollector);

            marshaller = jc.createMarshaller();
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        }
        if (charsetsToTry.isEmpty()) {
            tryToLoadCharset("ISO-8859-15");
            tryToLoadCharset("ISO-8859-1");
            tryToLoadCharset("UTF-8");
            tryToLoadCharset("UTF-16");
            tryToLoadCharset("UTF-32");
        }
        if (charsetsToTry.isEmpty()) {
            throw new JAXBException("Kein unterstütztes Charset vorhanden!");
        }
    }



    private void tryToLoadCharset(String charset) {
        try {
            charsetsToTry.add(Charset.forName(charset));
        } catch (IllegalArgumentException e) {
            SrtLogger.getLogger(getClass())
                    .debug()
                    .throwable(e)
                    .msgFormat("Kann Charset '%s' nicht laden: %s")
                    .msgArgs(charset, e.getMessage())
                    .flush();
        }
    }



    public ElsterRootElement unmarshal(byte[] data) throws JAXBException, SAXException {
        ElsterRootElement elster = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            elster = unmarshal(bais);
        } catch (IOException e) {
            // Probleme beim Schließen des Streams sollten nicht auftreten.
            throw new JAXBException(e);
        }
        return elster;
    }

    public ElsterRootElement unmarshal(InputStream is) throws JAXBException, SAXException {
        return (ElsterRootElement)getUnmarshaller().unmarshal(is);
    }

    public ElsterRootElement unmarshal(String xml) throws JAXBException, SAXException {
        init();
        validationEventCollector.reset();
        SAXException saxException = null;
        JAXBException jaxbException = null;
        for(Charset charset : charsetsToTry) {
            try (Reader rdr = new StringReader(xml); ReaderInputStream is = new ReaderInputStream(rdr, charset)) {
                return unmarshal(is);
            } catch (SAXException e) {
                saxException = e;
            } catch (JAXBException e) {
                jaxbException = e;
            } catch (IOException e) {
                // Probleme beim Schließen des Streams sollten nicht auftreten.
                throw new JAXBException(e);
            }
        }

        if (saxException != null) {
            throw tune(saxException, SAXException::new );
        }
        if (jaxbException != null) {
            throw tune(jaxbException, JAXBException::new );
        }
        // Kann logisch nicht vorkommen: unmarshall funktionierte oder eine Exception kam vorher.
        return null;
    }



    private <T extends Throwable> T tune(T exception, BiFunction<String, T, T> exceptionConstructor) {
        Optional<String> newErrorMessage = tunedErrorMessage();
        if (newErrorMessage.isPresent()) {
            return exceptionConstructor.apply(newErrorMessage.get(), exception);
        } else {
            return exception;
        }
    }

    private static List<String> importantErrorMessages = Arrays.asList(
            "cvc-complex-type",
            "cvc-datatype-valid",
            "cvc-enumeration-valid",
            "cvc-maxLength-valid",
            "cvc-pattern-valid"
    );

    private boolean isImportErrorMessage(String in) {
        return importantErrorMessages.stream()
                .filter( in::contains )
                .count() > 0;
    }

    private Optional<String> tunedErrorMessage() {
        return getValidationErrors()
                .stream()
                .filter( this::isImportErrorMessage )
                .findFirst();
    }



    public void marshal(ElsterRootElement jaxbElement, OutputStream os) throws JAXBException, SAXException {
        getMarshaller().marshal(jaxbElement, os);
    }

    public String marshal(ElsterRootElement jaxbElement) throws JAXBException, SAXException {
        return marshal(jaxbElement, false);
    }

    public String marshal(ElsterRootElement jaxbElement, boolean prettyPrint) throws JAXBException, SAXException {
        StringWriter sw = new StringWriter();
        getMarshaller().setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);
        getMarshaller().marshal(jaxbElement, sw);
        return sw.toString();
    }

    private Unmarshaller getUnmarshaller() throws SAXException, JAXBException {
        init();
        return unmarshaller;
    }

    private Marshaller getMarshaller() throws SAXException, JAXBException {
        init();
        return marshaller;
    }

    public List<String> getValidationErrors() {
        return Stream.of(validationEventCollector.getEvents())
                .map(ValidationEvent::getMessage)
                .distinct()
                .collect(Collectors.toList());
    }

}
*/
