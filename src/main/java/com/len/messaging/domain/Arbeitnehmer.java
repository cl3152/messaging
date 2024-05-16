package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the ARBEITNEHMER database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Arbeitnehmer.findAll", query="SELECT a FROM Arbeitnehmer a"),
    @NamedQuery(name="Arbeitnehmer.findSingle", query="SELECT a FROM Arbeitnehmer a WHERE a.transferId=:tID AND a.idnr=:idnr AND a.typ=:typ")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Arbeitnehmer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
        name="ARBEITNEHMER_ID_GENERATOR", sequenceName="S_ARBEITNEHMER_ID" ,
        initialValue=1, allocationSize=1
    )
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ARBEITNEHMER_ID_GENERATOR")
    private Long id;

    private String abmeldedat;
    private String bb;
    private String gebdat;
    private BigDecimal gewuenschterfb;
    private BigDecimal hag;
    private String idnr;
    private String refdatumag;
    private Long speichts;
    private String typ;

    //bi-directional many-to-one association to Transfer
    @Column(name="TRANSFER_ID")
    private Long transferId;

    public boolean isHauptarbeitgeber() {
        return hag == BigDecimal.ONE;
    }

    public void setHauptarbeitgeber(boolean istHauptarbeitgeber) {
        hag = istHauptarbeitgeber ? BigDecimal.ONE : BigDecimal.ZERO;
    }
}
