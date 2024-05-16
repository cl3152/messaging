package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Entity
@NamedQueries({
    @NamedQuery(name="Konfig.findAll", query="SELECT k FROM Konfig k"),
    @NamedQuery(name="Konfig.findSingle", query="SELECT k FROM Konfig k WHERE k.key=:key")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Konfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="KONFIG_ID_GENERATOR", sequenceName="S_KONFIG_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="KONFIG_ID_GENERATOR")
    private Long id;

    @Column(name = "\"KEY\"")
    // 'key' und 'value' sind reservierte Wörter in H2 und können nur quotiert verwendet werden.
    // Der H2Dialect übernimmt dies leider nicht automatisch, weshalb hier das Mapping auf die
    // quotierten Spaltennamen manuell erfolgen muss.
    // Vgl.
    // - http://www.h2database.com/html/advanced.html#keywords
    // - https://vladmihalcea.com/escape-sql-reserved-keywords-jpa-hibernate/
    private String key;

    @Column(name = "\"VALUE\"")
    // Zu @Column vgl #key
    private String value;

    public Konfig(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public long longValue() {
        return Long.parseLong(value);
    }
 }
