package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


/**
 * The persistent class for the AGVH database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Agvh.findAll", query="SELECT a FROM Agvh a"),
    @NamedQuery(name="Agvh.findSingle", query="SELECT a FROM Agvh a WHERE a.transferId=:tID AND a.hinweiseId=:hinweiseID")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Agvh implements Serializable, HinweisCommon {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="AGVH_ID_GENERATOR", sequenceName="S_AGVH_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AGVH_ID_GENERATOR")
    private Long id;

    private Long speichts;

    @Column(name="HINWEIS_ID")
    private Long hinweiseId;

    //bi-directional many-to-one association to Transfer
    @Column(name="TRANSFER_ID")
    private Long transferId;

    // Für das dynamische Erzeugen (Erkennen im Mapper, aber Anlegen erst mit Zugang zur DB möglich)
    @Transient
    private Hinweise hinweis;

    /** Enthält die Message des AG-Verfahrenhinweises, <b>wenn</b> diese vom Standard der Hinweis-Tabelle abweicht. */
    private String message;
}
