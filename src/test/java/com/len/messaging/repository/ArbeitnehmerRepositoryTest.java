package com.len.messaging.repository;

import com.len.messaging.domain.Arbeitnehmer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ArbeitnehmerRepositoryTest {

    @Autowired
    private ArbeitnehmerRepository arbeitnehmerRepository;

    private Arbeitnehmer arbeitnehmer;

    @BeforeEach
    public void setUp() {
        arbeitnehmer = new Arbeitnehmer();
        arbeitnehmer.setTransferId(1L);
        arbeitnehmer.setIdnr("12345");
        arbeitnehmer.setTyp("TypeA");
        arbeitnehmerRepository.save(arbeitnehmer);
    }

    @Test
    public void testFindByTransferIdAndIdnrAndTyp() {
        Optional<Arbeitnehmer> found = arbeitnehmerRepository.findByTransferIdAndIdnrAndTyp(1L, "12345", "TypeA");
        assertThat(found).isPresent();
        assertThat(found.get().getTransferId()).isEqualTo(1L);
        assertThat(found.get().getIdnr()).isEqualTo("12345");
        assertThat(found.get().getTyp()).isEqualTo("TypeA");
    }

/*    @Test
    public void testFindSingle() {
        Optional<Arbeitnehmer> found = arbeitnehmerRepository.findSingle(1L, "12345", "TypeA");
        assertThat(found).isPresent();
        assertThat(found.get().getTransferId()).isEqualTo(1L);
        assertThat(found.get().getIdnr()).isEqualTo("12345");
        assertThat(found.get().getTyp()).isEqualTo("TypeA");
    }

    @Test
    public void testFindSingle_NotFound() {
        Optional<Arbeitnehmer> found = arbeitnehmerRepository.findSingle(1L, "54321", "TypeB");
        assertThat(found).isNotPresent();
    }*/
}
