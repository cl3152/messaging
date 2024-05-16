package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


/**
 * The persistent class for the STNRH database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Stnrh.findAll", query="SELECT s FROM Stnrh s"),
    @NamedQuery(name="Stnrh.findSingle", query="SELECT a FROM Stnrh a WHERE a.transferId=:tID AND a.hinweiseId=:hinweiseID")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Stnrh implements Serializable, HinweisCommon {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="STNRH_ID_GENERATOR", sequenceName="S_STNRH_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="STNRH_ID_GENERATOR")
    private Long id;

    private Long speichts;

    private String stnr;

    //bi-directional many-to-one association to Hinweise
    @Column(name="HINWEIS_ID")
    private Long hinweiseId;

    //bi-directional many-to-one association to Transfer
    @Column(name="TRANSFER_ID")
    private Long transferId;

    // Für das dynamische Erzeugen (Erkennen im Mapper, aber Anlegen erst mit Zugang zur DB möglich)
    @Transient
    private Hinweise hinweis;
}
