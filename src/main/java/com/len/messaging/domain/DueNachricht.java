package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


/**
 * The persistent class for the DUE_NACHRICHT database table.
 *
 */
@Entity
@Table(name="DUE_NACHRICHT")
@NamedQueries({
    @NamedQuery(name="DueNachricht.findAll", query="SELECT d FROM DueNachricht d"),
    @NamedQuery(name="DueNachricht.findSingle", query="SELECT d FROM DueNachricht d WHERE d.transferId=:tID AND d.typ=:typ")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DueNachricht implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
        name="DUE_NACHRICHT_ID_GENERATOR", sequenceName="S_DUE_NACHRICHT_ID",
        initialValue=1, allocationSize=1
    )
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DUE_NACHRICHT_ID_GENERATOR")
    private Long id;
    private String anzahlteillisten;
    private String art;
    private String laufendenr;
    private String refdatumag;
    private Long speichts;
    private String stnrag;
    private String stnrdue;
    private String dueZobelId;
    private Boolean dueZobelIDIstLebend;
    private String dueAktuelleSteuernummer;
    private Long accountid;

    /**
     * Datenart aus dem TransferHeader.
     */
    private String typ;

    //bi-directional many-to-one association to Schemaversion
    @Column(name="SCHEMA_ID")
    private Long schemaversionId;

    //bi-directional many-to-one association to Transfer
    @Column(name="TRANSFER_ID")
    private Long transferId;
}
