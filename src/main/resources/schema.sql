-- Erstellung der Transfer-Tabelle
CREATE TABLE IF NOT EXISTS transfer (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        number VARCHAR(255) NOT NULL
);

-- Erstellung der Agvh-Tabelle
CREATE TABLE IF NOT EXISTS agvh (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    text VARCHAR(255) NOT NULL,
                                    transfer_id BIGINT NOT NULL,
                                    CONSTRAINT fk_transfer FOREIGN KEY (transfer_id) REFERENCES transfer(id)
);
