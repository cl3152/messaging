-- Erstellen der Tabelle TRANSFER
CREATE TABLE TRANSFER (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          eingangts BIGINT,
                          herstellerid BIGINT,
                          nutzdatenticket VARCHAR(255),
                          speichts BIGINT,
                          transferticket VARCHAR(255)
);

-- Erstellen der Tabelle ARBEITNEHMER
CREATE TABLE ARBEITNEHMER (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              abmeldedat VARCHAR(255),
                              bb VARCHAR(255),
                              gebdat VARCHAR(255),
                              gewuenschterfb DECIMAL(19, 2),
                              hag DECIMAL(19, 2),
                              idnr VARCHAR(255),
                              refdatumag VARCHAR(255),
                              speichts BIGINT,
                              typ VARCHAR(255),
                              transferId BIGINT,
                              CONSTRAINT fk_transfer FOREIGN KEY (transferId) REFERENCES TRANSFER(id)
);