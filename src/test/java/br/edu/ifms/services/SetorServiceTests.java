package br.edu.ifms.services;

import br.edu.ifms.dto.SetorDTO;
import br.edu.ifms.entities.Setor;
import br.edu.ifms.repositories.SetorRepository;
import br.edu.ifms.services.exceptions.DataBaseException;
import br.edu.ifms.services.exceptions.ResourceNotFoundException;
import br.edu.ifms.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

/**
 * Classe de testes para o serviço SetorService.
 */
@ExtendWith(SpringExtension.class)
public class SetorServiceTests {

    @InjectMocks
    private SetorService service;

    @Mock
    private SetorRepository repository;

    private long idExistente;
    private long idInexistente;
    private long idDependente;
    private SetorDTO setorDTO;

    /**
     * Configuração inicial dos testes.
     *
     * @throws Exception se ocorrer algum erro durante a configuração
     */
    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 1000L;
        idDependente = 4L;
        long totalSetores = 3L;
        Setor setor = Factory.createSetor();
        setorDTO = Factory.createSetorDTO();
        PageImpl<Setor> page = new PageImpl<>(List.of(setor));

        // Configuração dos comportamentos simulados
        Mockito.doNothing().when(repository).deleteById(idExistente);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(idInexistente);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idDependente);

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(setor);
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(setor));
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(setor);
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
    }

    /**
     * Testa se o método delete lança DataBaseException quando o ID é dependente.
     */
    @Test
    public void deleteDeveriaLancarDataBaseExceptionQuandoIdDependente() {
        Assertions.assertThrows(DataBaseException.class, () -> {
            service.delete(idDependente);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(idDependente);
    }

    /**
     * Testa se o método delete lança ResourceNotFoundException quando o ID é inexistente.
     */
    @Test
    public void deleteDeveriaLancarResourceNotFoundExceptionQuandoIdInexistente() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(idInexistente);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(idInexistente);
    }

    /**
     * Testa se o método delete não lança exceção quando o ID é existente.
     */
    @Test
    public void deleteDeveriaFazerNadaQuandoIdExistente() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(idExistente);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(idExistente);
    }

    /**
     * Testa se o método findAllPaged retorna uma página de SetorDTO.
     */
    @Test
    public void findAllPagedDeveriaRetornarPagina() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<SetorDTO> result = service.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    /**
     * Testa se o método findById retorna SetorDTO quando o ID é existente.
     */
    @Test
    public void findByIdDeveriaRetornarSetorDTOQuandoIdExistente() {
        SetorDTO result = service.findById(idExistente);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findById(idExistente);
    }

    /**
     * Testa se o método findById lança ResourceNotFoundException quando o ID é inexistente.
     */
    @Test
    public void findByIdDeveriaLancarResourceNotFoundExceptionQuandoIdInexistente() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(idInexistente);
        });
        Mockito.verify(repository, Mockito.times(1)).findById(idInexistente);
    }

    /**
     * Testa se o método insert retorna SetorDTO.
     */
    @Test
    public void insertDeveriaRetornarSetorDTO() {
        SetorDTO result = service.insert(setorDTO);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any());
    }

    /**
     * Testa se o método update retorna SetorDTO quando o ID é existente.
     */
    @Test
    public void updateDeveriaRetornarSetorDTOQuandoIdExistente() {
        SetorDTO result = service.update(idExistente, setorDTO);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).getReferenceById(idExistente);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any());
    }

    /**
     * Testa se o método update lança ResourceNotFoundException quando o ID é inexistente.
     */
    @Test
    public void updateDeveriaLancarResourceNotFoundExceptionQuandoIdInexistente() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(idInexistente, setorDTO);
        });
        Mockito.verify(repository, Mockito.times(1)).getReferenceById(idInexistente);
    }
}