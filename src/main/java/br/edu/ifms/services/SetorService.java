package br.edu.ifms.services;

import br.edu.ifms.dto.SetorDTO;
import br.edu.ifms.entities.Setor;
import br.edu.ifms.repositories.SetorRepository;
import br.edu.ifms.services.exceptions.DataBaseException;
import br.edu.ifms.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe de serviço para gerenciar entidades Setor.
 */
@Service
public class SetorService {

    @Autowired
    private SetorRepository repository;

    /**
     * Recupera todas as entidades Setor e as converte para SetorDTO.
     *
     * @return uma lista de SetorDTO
     */
    @Transactional(readOnly = true)
    public List<SetorDTO> findAll() {
        List<Setor> list = repository.findAll();
        return list.stream().map(SetorDTO::new).collect(Collectors.toList());
    }

    /**
     * Recupera uma lista paginada de entidades Setor e as converte para SetorDTO.
     *
     * @param pageable as informações de paginação
     * @return uma lista paginada de SetorDTO
     */
    @Transactional(readOnly = true)
    public Page<SetorDTO> findAllPaged(Pageable pageable) {
        Page<Setor> list = repository.findAll(pageable);
        return list.map(SetorDTO::new);
    }

    /**
     * Recupera uma entidade Setor pelo seu ID e a converte para SetorDTO.
     *
     * @param id o ID da entidade Setor
     * @return o SetorDTO
     * @throws ResourceNotFoundException se a entidade não for encontrada
     */
    @Transactional(readOnly = true)
    public SetorDTO findById(Long id) {
        Optional<Setor> obj = repository.findById(id);
        Setor entity = obj.orElseThrow(() -> new ResourceNotFoundException(
                "A entidade consultada não foi localizada"));
        return new SetorDTO(entity);
    }

    /**
     * Insere uma nova entidade Setor com base no SetorDTO fornecido.
     *
     * @param dto o SetorDTO contendo os dados para inserir
     * @return o SetorDTO inserido
     */
    @Transactional
    public SetorDTO insert(SetorDTO dto) {
        Setor entity = new Setor();
        return getSetorDTO(dto, entity);
    }

    /**
     * Método auxiliar para definir as propriedades de uma entidade Setor a partir de um SetorDTO e salvá-la.
     *
     * @param dto    o SetorDTO contendo os dados
     * @param entity a entidade Setor a ser atualizada
     * @return o SetorDTO atualizado
     */
    private SetorDTO getSetorDTO(SetorDTO dto, Setor entity) {
        entity.setSigla(dto.getSigla());
        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setTelefone(dto.getTelefone());
        entity.setCoordenador(dto.getCoordenador());
        entity = repository.save(entity);
        return new SetorDTO(entity);
    }

    /**
     * Atualiza uma entidade Setor existente com base no SetorDTO fornecido.
     *
     * @param id  o ID da entidade Setor a ser atualizada
     * @param dto o SetorDTO contendo os dados para atualizar
     * @return o SetorDTO atualizado
     * @throws ResourceNotFoundException se a entidade não for encontrada
     */
    @Transactional
    public SetorDTO update(Long id, SetorDTO dto) {
        try {
            Setor entity = repository.getReferenceById(id);
            return getSetorDTO(dto, entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("O recurso com o ID = " + id + " não foi localizado");
        }
    }

    /**
     * Exclui uma entidade Setor pelo seu ID.
     *
     * @param id o ID da entidade Setor a ser excluída
     * @throws ResourceNotFoundException se a entidade não for encontrada
     * @throws DataBaseException         se a entidade não puder ser excluída devido a violação de integridade de dados
     */
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("O recurso com o ID = " + id + "não foi localizado");
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Não é possível excluir o registro, pois o mesmo está em uso");
        }
    }
}