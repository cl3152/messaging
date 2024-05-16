package com.len.messaging.util.constants;

import java.util.Arrays;
import java.util.Optional;

public enum Datenart {

    // Arbeitnehmerauskunft
    ANAUSKUNFT("ANAuskunft"),
    ANAUSKUNFT_ELSTAM("ANAuskunftElstam"),
    ANAUSKUNFT_REQUEST("ANAuskunftRequest"),
    // Arbeitgeberschnittstelle
    DUE_ANMELDEN("DUeAnmelden"),
    DUE_ABMELDEN("DUeAbmelden"),
    DUE_UMMELDEN("DUeUmmelden"),
    AENDERUNGSLISTE_DUE("AenderungslisteDUe"),
    AENDERUNGSLISTE_ELSTAM("AenderungslisteElstam"),
    AENDERUNGSLISTE_HINWEIS("AenderungslisteHinweis"),
    AENDERUNGSLISTE_STATUS("AenderungslisteStatus"),
    // Dialog
    DIALOG("Dialog"),
    // ELO2-"interne" Kommunikation
    ELSTAM_SUCHE("ELStAMSuche"),
    ELSTERLOHN2_ANDATEN("ElsterLohn2ANDaten"),
    ELSTERLOHN2_DATEN("ElsterLohn2Daten"),
    IDNR_SUCHE("IDNrSuche")
    ;

    private String asString;

    private Datenart(String asString) {
        this.asString = asString;
    }

    public static Optional<Datenart> parse(String datenart) {
        for(Datenart d : values()) {
            if (d.asString.equals(datenart)) {
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    public String asString() {
        return asString;
    }

    public boolean isDueRequest() {
        return Arrays.asList(DUE_ABMELDEN, DUE_ANMELDEN, DUE_UMMELDEN).contains(this);
    }

    public boolean isDueResponse() {
        return Arrays.asList(AENDERUNGSLISTE_DUE, AENDERUNGSLISTE_ELSTAM).contains(this);
    }

}
