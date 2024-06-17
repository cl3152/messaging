package com.len.messaging.repository;

import com.len.messaging.domain.Fehler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FehlerRepository extends JpaRepository<Fehler, Long> {

    @Query(value = "SELECT count(*) FROM SRT_FEHLER WHERE wieder_einspielen = 1", nativeQuery = true)
    long countWiederEinzuspielen();

    @Modifying
    @Transactional
    @Query(value = "UPDATE SRT_FEHLER SET wieder_einspielen = 1 WHERE errormessage LIKE ?1", nativeQuery = true)
    void markForWiederEinzuspielen(String like);

    List<Fehler> findByTransferticket(String tt);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM srt_fehler WHERE rowid NOT IN (SELECT first_value(rowid) OVER (partition BY rohdaten_hash) FROM srt_fehler) AND rohdaten_hash IS NOT NULL", nativeQuery = true)
    void deleteDuplicates();

    @Query(value = "SELECT f FROM Fehler f WHERE f.rohdatenHash IS NULL")
    List<Fehler> findWhereRohdatenHashIsNull();

    Optional<Fehler> findByRohdatenHash(String hash);
}