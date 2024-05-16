package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the FB database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Fb.findAll", query="SELECT f FROM Fb f"),
})
@Data
@EqualsAndHashCode(exclude={"elstam"})
@ToString(exclude={"elstam"})
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("common-java:DuplicatedBlocks") // generierter Code
public class Fb implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="FB_ID_GENERATOR", sequenceName="S_FB_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="FB_ID_GENERATOR")
    private Long id;

    private Long jahr;

    private Long monat;

    private Long speichts;

    private BigDecimal tag;

    private BigDecimal woche;

    //bi-directional many-to-one association to Elstam
    @OneToOne
    private Elstam elstam;
}
