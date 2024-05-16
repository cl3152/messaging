package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

@Entity
@Table(name="SRT_FEHLER")
@NamedQueries({
    @NamedQuery(name="Fehler.findAll", query="SELECT f FROM Fehler f"),
    @NamedQuery(name="Fehler.findByTTandNDT", query="SELECT f FROM Fehler f WHERE f.transferticket=:tt AND f.nutzdatenticket=:ndt")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Fehler implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Maximal-Länge für den Fehlertext, wie er durch die DB vorgegeben wird: {@value}
     * 4000 Zeichen bilden den Maximalwert für Oracle VARCHAR2.
     */
    private static final int MAX_SIZE_MESSAGE = 255;

    /**
     * Maximal-Länge für den SHA-Hash über die Rohdaten, wie er durch die DB vorgegeben wird
     * und von Apache Commons Code generiert wird: {@value}.
     */
    private static final int MAX_SIZE_ROHDATENHASH = 64;



    @Id
    @SequenceGenerator(
        name="SRT_FEHLER_ID_GENERATOR", sequenceName="S_SRT_FEHLER_ID" ,
        initialValue=1, allocationSize=1
    )
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SRT_FEHLER_ID_GENERATOR")
    private Long id;
    private String transferticket;
    private String nutzdatenticket;

    @Column(length=MAX_SIZE_MESSAGE)
    private String errorMessage;

    @Lob
    private String stacktrace;

    private Long speichts;
    private String rohdaten;

    @Column(name="ROHDATEN_HASH")
    private String rohdatenHash;

    @Column(name="WIEDER_EINSPIELEN")
    private int wiederEinspielen;



    public Fehler transferticket(String tt) {
        transferticket = tt;
        return this;
    }

    public Fehler ndTicket(String ndt) {
        nutzdatenticket = ndt;
        return this;
    }

    public Fehler message(String message) {
        setErrorMessage(message);
        return this;
    }

    public Fehler stacktrace(String trace) {
        setStacktrace(trace);
        return this;
    }

    public Fehler stacktrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        setStacktrace(sw.toString());
        return this;
    }

    public Fehler speichts(Long speichts) {
        setSpeichts(speichts);
        return this;
    }

    @SuppressWarnings("squid:S4790") // Hash dient nur zur Duplikatserkennung.
    public Fehler rohdaten(String rohdaten) {
        setRohdaten(rohdaten);
        setRohdatenHash(shorten(DigestUtils.sha256Hex(rohdaten), MAX_SIZE_ROHDATENHASH));
        return this;
    }

    public Fehler wiederEinspielen() {
        setWiederEinspielen(true);
        return this;
    }

    public Fehler wiederEinspielen(boolean wiederEinspielen) {
        setWiederEinspielen(wiederEinspielen);
        return this;
    }



    public void setErrorMessage(String errorMessage) {
        this.errorMessage = shorten(errorMessage, MAX_SIZE_MESSAGE);
    }

    public String getRohdatenAsString() {
        return rohdaten;
    }

    public boolean isWiederEinspielen() {
        return wiederEinspielen == 1;
    }

    public void setWiederEinspielen(boolean value) {
        setWiederEinspielen(value ? 1 : 0);
    }

    public int getWiederEinspielen() {
        return wiederEinspielen;
    }

    public void setWiederEinspielen(int value) {
        wiederEinspielen = value;
    }

    public Status getStatus() {
        return Status.parse(wiederEinspielen);
    }

    public void setStatus(Status status) {
        wiederEinspielen = status == null ? 0 : status.getValue();
    }



    public enum Status {
        EINGESPIELT(0),
        WIEDER_EINSPIELEN(1),
        TO_DELETE(2);
        private int value;
        private Status(int value) {
            this.value = value;
        }
        public static Status parse(int value) {
            for(Status s : values()) {
                if (s.getValue() == value) {
                    return s;
                }
            }
            return null;
        }
        public int getValue() {
            return value;
        }
    }



    /**
     * Kürzt einen String auf die angegebene Maximallänge.
     * @param string zu kürzender String
     * @param maxSize Maximallänge
     * @return ggfs. gekürzter String
     */
    protected static String shorten(String string, int maxSize) {
        if (string == null || string.length() <= maxSize) {
            return string;
        } else if (maxSize > 0) {
            return string.substring(0, maxSize);
        } else {
            return "";
        }
    }

}
