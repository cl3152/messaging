package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teillieferung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="TEILLIEFERUNG_ID_GENERATOR", sequenceName="S_TEILLIEFERUNG_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="TEILLIEFERUNG_ID_GENERATOR")
    private Long id;

    private String nutzdatenticket;

    @OneToOne
    @JoinColumn(name="TRANSFER_ID")
    private Transfer transfer;
}
