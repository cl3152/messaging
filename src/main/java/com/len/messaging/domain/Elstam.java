package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the ELSTAM database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Elstam.findAll", query="SELECT e FROM Elstam e"),
    @NamedQuery(name="Elstam.findSingle", query="SELECT e FROM Elstam e WHERE e.transferId=:tID AND e.idnr=:idnr"),
    @NamedQuery(name="Elstam.findByIdnr", query="SELECT e FROM Elstam e WHERE e.idnr=:idnr")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Elstam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="ELSTAM_ID_GENERATOR", sequenceName="S_ELSTAM_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ELSTAM_ID_GENERATOR")
    private Long id;

    private BigDecimal faktor;

    private String gueltigab;

    private String idnr;

    private BigDecimal kinderfb;

    private String konfession;

    @Column(name="KONFESSION_PARTNER")
    private String konfessionPartner;

    private Long speichts;

    private BigDecimal steuerklasse;

    //bi-directional many-to-one association to Transfer
    @Column(name="TRANSFER_ID")
    private Long transferId;

    //bi-directional many-to-one association to Fb
    @OneToOne(mappedBy="elstam", cascade=CascadeType.ALL)
    private Fb fb;

    //bi-directional many-to-one association to Hinzub
    @OneToOne(mappedBy="elstam", cascade=CascadeType.ALL)
    private Hinzub hinzub;
}
