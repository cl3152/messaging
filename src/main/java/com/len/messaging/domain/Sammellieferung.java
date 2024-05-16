package com.len.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sammellieferung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="SAMMELLIEFERUNG_ID_GENERATOR", sequenceName="S_SAMMELLIEFERUNG_ID", initialValue=1, allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SAMMELLIEFERUNG_ID_GENERATOR")
    private Long id;

    private Long speichts;
    private String transferticket;

    @OneToMany(cascade= CascadeType.ALL)
    @JoinColumn(name="SAMMEL_ID")
    private List<Teillieferung> teillieferungen;

    public boolean add(Teillieferung teil) {
        if (teillieferungen == null) {
            teillieferungen = new ArrayList<>();
        }
        return teillieferungen.add(teil);
    }

    public Teillieferung getByNutzdatenticket(String nutzdatenticket) {
        for(Teillieferung teil : getTeillieferungen()) {
            if (teil.getNutzdatenticket().equals(nutzdatenticket)) {
                return teil;
            }
        }
        return null;
    }

    public void add(Transfer transfer) {
        if (transfer != null) {
            if (StringUtils.equals(transferticket, transfer.getTransferticket())) {
                Teillieferung teillieferung = getByNutzdatenticket(transfer.getNutzdatenticket());
                if (teillieferung != null) {
                    teillieferung.setTransfer(transfer);
                } else {
                    throw new IllegalArgumentException(String.format(
                        "Sammellieferung tt='%s' enthält keine Teillieferung mit ndt='%s'",
                        transferticket, transfer.getNutzdatenticket()
                    ));
                }
            } else {
                throw new IllegalArgumentException(String.format(
                    "Fehlerhafter Versuch Transfer mit Sammellieferung zu verbinden: tt='%s' transfer.tt='%s'",
                    transferticket, transfer.getTransferticket()
                ));
            }
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Sammellieferung(tt='%s', speichts=%s, id=%s, teile.count=%s)",
            transferticket,
            speichts,
            id,
            teillieferungen != null ? teillieferungen.size() : 0
        );
    }
}
