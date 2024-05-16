package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


/**
 * The persistent class for the SCHEMAVERSION database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Schemaversion.findAll", query="SELECT s FROM Schemaversion s"),
    @NamedQuery(name="Schemaversion.findSingle", query="SELECT s FROM Schemaversion s WHERE s.schema=:xmlns")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Schemaversion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
        name="SCHEMAVERSION_ID_GENERATOR", sequenceName="S_SCHEMAVERSION_ID",
        initialValue=1, allocationSize=1
    )
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SCHEMAVERSION_ID_GENERATOR")
    private Long id;

    @Column(name="\"SCHEMA\"")
    private String schema;
}
