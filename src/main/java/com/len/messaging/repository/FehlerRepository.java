package com.len.messaging.repository;

import com.len.messaging.domain.Fehler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FehlerRepository extends JpaRepository<Fehler, Long> {


}
