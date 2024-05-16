package com.len.messaging.util;

import static com.len.messaging.util.constants.Datenart.AENDERUNGSLISTE_DUE;
import static com.len.messaging.util.constants.Datenart.AENDERUNGSLISTE_ELSTAM;
import static com.len.messaging.util.constants.Datenart.DUE_ABMELDEN;
import static com.len.messaging.util.constants.Datenart.DUE_ANMELDEN;
import static com.len.messaging.util.constants.Datenart.DUE_UMMELDEN;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.len.messaging.util.constants.Datenart;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ElsterInfo {

    @Getter(value=AccessLevel.PRIVATE)
    private static List<String> elo2Datenarten = Stream.of(
            DUE_ANMELDEN, DUE_ABMELDEN, DUE_UMMELDEN,
            AENDERUNGSLISTE_DUE, AENDERUNGSLISTE_ELSTAM
    ).map(Datenart::asString).toList();

    public enum Step {
        DUE_REQUEST, ELSTAM_REQUEST, ELSTAM_RESPONSE, DUE_RESPONSE
    }

    private String verfahren;
    private String datenart;
    private String ziel;
    private String transferticket;
    private String erstesNutzdatenticket;


    public boolean istElstamNachricht() {
        boolean istRequest = isRequest();
        return istRequest && "BF".equals(ziel)
                || istRequest && "BFF".equals(ziel)
                || datenartEquals(AENDERUNGSLISTE_ELSTAM);
    }

    public boolean isRequest() {
        return datenartEquals(DUE_ABMELDEN)
                || datenartEquals(DUE_ANMELDEN)
                || datenartEquals(DUE_UMMELDEN);
    }

    public Step getStep() {
        if (ziel == null && datenart == null) {
            // Nicht initialisiert
            return null;
        } else {
            if (isRequest()) {
                return "CS".equals(ziel) ? Step.DUE_REQUEST : Step.ELSTAM_REQUEST;
            } else {
                return datenartEquals(AENDERUNGSLISTE_ELSTAM) ? Step.ELSTAM_RESPONSE : Step.DUE_RESPONSE;
            }
        }
    }

    public boolean isElo() {
        return isEloVerfahren() && isEloDatenart();
    }

    public boolean isEloDatenart() {
        return elo2Datenarten.contains(datenart);
    }

    public boolean isEloVerfahren() {
        return "ElsterLohn2".equals(verfahren);
    }

    private boolean datenartEquals(Datenart expected) {
        return expected.asString().equals(datenart);
    }

}
