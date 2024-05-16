package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


/**
 * The persistent class for the ELSTAM_NACHRICHT database table.
 *
 */
@Entity
@Table(name="ELSTAM_NACHRICHT")
@NamedQueries({
    @NamedQuery(name="ElstamNachricht.findSingle", query="SELECT e FROM ElstamNachricht e WHERE e.transferId=:tID AND e.typ=:typ"),
    @NamedQuery(name="ElstamNachricht.findAll", query="SELECT e FROM ElstamNachricht e"),
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ElstamNachricht implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
        name="ELSTAM_NACHRICHT_ID_GENERATOR", sequenceName="S_ELSTAM_NACHRICHT_ID",
        initialValue=1, allocationSize=1
    )
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ELSTAM_NACHRICHT_ID_GENERATOR")
    private Long id;

    private String anzahlteillisten;
    private String art;
    private String bundesland;
    private String laufendenr;
    private String refdatumag;
    private Long speichts;

    /**
     * Datenart aus dem TransferHeader.
     */
    private String typ;
    private String zidag;
    private String ziddue;

    @Column(name="SCHEMA_ID")
    private Long schemaversionId;

    //bi-directional many-to-one association to Transfer
    @Column(name="TRANSFER_ID")
    private Long transferId;
}
