package com.len.messaging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 5000) // Alle 5 Sekunden
    public void checkDatabaseConnection() {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    }
}
