package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


/**
 * The persistent class for the HINWEISE database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Hinweise.findAll", query="SELECT h FROM Hinweise h"),
    @NamedQuery(name="Hinweise.findSingle", query="SELECT h FROM Hinweise h WHERE h.code=:code")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Hinweise implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="HINWEISE_ID_GENERATOR", sequenceName="S_HINWEISE_ID" , initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="HINWEISE_ID_GENERATOR")
    private Long id;

    private String code;

    private String message;

    private String type;
}
