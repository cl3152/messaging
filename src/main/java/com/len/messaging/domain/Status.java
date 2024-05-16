package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


/**
 * The persistent class for the STATUS database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Status.findAll", query="SELECT s FROM Status s"),
    @NamedQuery(name="Status.findSingle", query="SELECT s FROM Status s WHERE s.key=:key")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="STATUS_ID_GENERATOR", sequenceName="S_STATUS_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STATUS_ID_GENERATOR")
    private Long id;

    @Column(name="\"KEY\"")
    // Vgl. Konfig#key
    private String key;

    @Column(name="\"VALUE\"")
    // Vgl. Konfig#key
    private String value;

    public Status(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
