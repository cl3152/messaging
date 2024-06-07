package com.len.messaging.repository;

import com.len.messaging.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByTransferticketAndNutzdatenticket(@Param("transferTicket") String transferTicket, @Param("firstNutzdatenTicket") String firstNutzdatenTicket);

    List<Transfer> findByTransferticket(@Param("transferTicket") String transferTicket);
}