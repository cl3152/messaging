package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


/**
 * The persistent class for the ANVH database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Anvh.findAll", query="SELECT a FROM Anvh a"),
    @NamedQuery(name="Anvh.findSingle", query="SELECT a FROM Anvh a WHERE a.transferId=:tID AND a.hinweiseId=:hinweiseID AND a.idnr=:idnr")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Anvh implements Serializable, HinweisCommon {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="ANVH_ID_GENERATOR", sequenceName="S_ANVH_ID" , initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ANVH_ID_GENERATOR")
    private Long id;

    private String datum;

    private String datumalt;

    private String idnr;

    private Long speichts;

    //bi-directional many-to-one association to Hinweise
    @Column(name="HINWEISE_ID")
    private Long hinweiseId;

    //bi-directional many-to-one association to Transfer
    @Column(name="TRANSFER_ID")
    private Long transferId;

    // Für das dynamische Erzeugen (Erkennen im Mapper, aber Anlegen erst mit Zugang zur DB möglich)
    @Transient
    private Hinweise hinweis;
}
