package br.edu.ifms.repositories;

import br.edu.ifms.entities.Tecnico;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class TecnicoRepositoryTests {

    @Autowired
    private TecnicoRepository repository;

    @Test
    public void consultaDeveriaRetornarObjetoQuandoIdExistir() {

        long idExistente = 10L;

        repository.findById(idExistente);

        Optional<Tecnico> resultado = repository.findById(idExistente);

        assertTrue(resultado.isPresent());

    }

}