package br.edu.ifms.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.edu.ifms.entities.Tecnico;
import br.edu.ifms.repositories.TecnicoRepository;

@ExtendWith(SpringExtension.class)
public class TecnicoServiceTests {

	@InjectMocks
	private TecnicoService service;
	
	@Mock
	private TecnicoRepository repository;
	
	private long idExistente;
	private long idInexistente;
	private long totalTecnicos;
	
	@BeforeEach
	void setUp() throws Exception{
		idExistente = 2L;
		idInexistente = 30L;
		totalTecnicos = 3L;
	}
	
	@Test
	public void deleteDeveriaExcluirObjetoQuandoIdExistente() {
		
		repository.deleteById(idExistente);
		
		Optional<Tecnico> resultado = repository.findById(idExistente);
		Assertions.assertFalse(resultado.isPresent());
	}
}





