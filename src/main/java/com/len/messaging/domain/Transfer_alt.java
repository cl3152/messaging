/*
package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


*/
/**
 * The persistent class for the TRANSFER database table.
 *
 *//*

@Entity
@NamedQueries({
    @NamedQuery(name="Transfer.findAll", query="SELECT t FROM Transfer t"),
    @NamedQuery(name="Transfer.findSingle", query="SELECT t FROM Transfer t WHERE t.transferticket=:tt AND t.nutzdatenticket=:ndt"),
    @NamedQuery(name="Transfer.findByTransferticketAndNDTicket", query="SELECT t FROM Transfer t WHERE t.transferticket=:tt AND t.nutzdatenticket=:ndt")
})
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transfer_alt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="TRANSFER_ID_GENERATOR", sequenceName="S_TRANSFER_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TRANSFER_ID_GENERATOR")
    private Long id;

    private Long eingangts;

    private Long herstellerid;

    private String nutzdatenticket;

    private Long speichts;

    private String transferticket;
}
*/
