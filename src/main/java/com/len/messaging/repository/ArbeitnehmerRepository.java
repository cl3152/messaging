package com.len.messaging.repository;

import com.len.messaging.domain.Arbeitnehmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArbeitnehmerRepository extends JpaRepository<Arbeitnehmer, Long> {

    Optional<Arbeitnehmer> findByTransferIdAndIdnrAndTyp(@Param("transferId") long transferId,
                                                         @Param("idnr") String idnr,
                                                         @Param("typ") String typ);

    default Optional<Arbeitnehmer> findSingle(long transferId, String idnr, String typ) {
        return findByTransferIdAndIdnrAndTyp(transferId, idnr, typ);
    }
}