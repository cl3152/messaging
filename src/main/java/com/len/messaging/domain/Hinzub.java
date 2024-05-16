package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@NamedQueries({
    @NamedQuery(name="Hinzub.findAll", query="SELECT h FROM Hinzub h"),
})
@Data
@EqualsAndHashCode(exclude={"elstam"})
@ToString(exclude={"elstam"})
@AllArgsConstructor
@NoArgsConstructor
public class Hinzub implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="HINZUB_ID_GENERATOR", sequenceName="S_HINZUB_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="HINZUB_ID_GENERATOR")
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
