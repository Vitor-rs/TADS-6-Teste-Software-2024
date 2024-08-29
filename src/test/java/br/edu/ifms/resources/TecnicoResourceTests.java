package br.edu.ifms.resources;

import br.edu.ifms.dto.TecnicoDTO;
import br.edu.ifms.services.TecnicoService;
import br.edu.ifms.services.exceptions.DataBaseException;
import br.edu.ifms.services.exceptions.ResourceNotFoundException;
import br.edu.ifms.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classe de testes para o recurso TecnicoResource.
 * <p>
 * Esta classe contém testes unitários que verificam o comportamento da classe
 * TecnicoResource, que é responsável por gerenciar as operações relacionadas
 * aos técnicos. Os testes são realizados utilizando o MockMvc para simular
 * requisições HTTP e verificar as respostas da API.
 * <p>
 * Os testes incluem:
 * - Atualização de técnicos
 * - Busca de técnicos por ID
 * - Listagem paginada de técnicos
 * <p>
 * Os testes utilizam mocks do serviço TecnicoService para isolar a lógica
 * de controle da lógica de serviço, permitindo verificar se as respostas
 * da API estão corretas.
 *
 * @see TecnicoResource
 */
@WebMvcTest(TecnicoResource.class)
public class TecnicoResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TecnicoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private TecnicoDTO tecnicoDTO;
    private PageImpl<TecnicoDTO> page;
    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;

    /**
     * Configuração inicial dos testes.
     * <p>
     * Este método é executado antes de cada teste, configurando os dados
     * necessários e as respostas esperadas do serviço mockado.
     *
     * @throws Exception se ocorrer um erro durante a configuração.
     */
    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 100L;

        tecnicoDTO = Factory.createTecnicoDTO();
        page = new PageImpl<>(List.of(tecnicoDTO));

        when(service.findAllPaged(any())).thenReturn(page);
        when(service.findById(idExistente)).thenReturn(tecnicoDTO);
        when(service.findById(idInexistente)).thenThrow(ResourceNotFoundException.class);

        // UPDATE
        when(service.update(eq(idExistente), any())).thenReturn(tecnicoDTO);
        when(service.update(eq(idInexistente), any())).thenThrow(ResourceNotFoundException.class);

        // Insert
        when(service.insert(any())).thenReturn(tecnicoDTO);

        // Delete
        doNothing().when(service).delete(idExistente);
        doThrow(ResourceNotFoundException.class).when(service).delete(idInexistente);
        doThrow(DataBaseException.class).when(service).delete(idDependente);
    }

    /**
     * Testa a atualização de um técnico existente.
     * <p>
     * Este teste verifica se a atualização de um técnico com um ID existente
     * retorna um status 200 OK e se o corpo da resposta contém os dados
     * do técnico atualizado.
     *
     * @throws Exception se ocorrer um erro durante a execução do teste.
     */
    @Test
    public void updateDeveriaRetornarTecnicoQuandoIdExistente() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);
        ResultActions result = mockMvc.perform(put("/tecnicos/{id}", idExistente)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.nome").exists());
    }

    /**
     * Testa a atualização de um técnico com um ID inexistente.
     * <p>
     * Este teste verifica se a tentativa de atualização de um técnico com
     * um ID que não existe retorna um status 404 Not Found.
     *
     * @throws Exception se ocorrer um erro durante a execução do teste.
     */
    @Test
    public void updateDeveriaRetornarNotFoundQuandoIdInexistente() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);
        ResultActions result = mockMvc.perform(put("/tecnicos/{id}", idInexistente)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    // Exercício
    //insert deveria retornar um "created" (código 201) e um TecnicoDTO
    @Test
    public void insertDeveriaRetornarCreatedETecnicoDTO() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);
        ResultActions result = mockMvc.perform(post("/tecnicos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.nome").exists());
    }

    /*
    * delete deveria retornar um "no content" (código 204) quando o ID existir
    * */
    @Test
    public void deleteDeveriaRetornarNoContentQuandoIdExistir() throws Exception {
        ResultActions result = mockMvc.perform(delete("/tecnicos/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }

    /*
    * delete deveria retornar um "not found" (código 404) quando o ID não existir
    * */
    @Test
    public void deleteDeveriaRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions result = mockMvc.perform(delete("/tecnicos/{id}", idInexistente)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    /**
     * Testa a listagem paginada de técnicos.
     * <p>
     * Este teste verifica se a requisição para listar todos os técnicos
     * retorna um status 200 OK.
     *
     * @throws Exception se ocorrer um erro durante a execução do teste.
     */
    @Test
    public void findAllPagedDeveriaRetornarPage() throws Exception {
        ResultActions result = mockMvc.perform(get("/tecnicos")
                .accept(MediaType.APPLICATION_JSON)
        );
        result.andExpect(status().isOk());
    }

    /**
     * Testa a busca de um técnico por ID existente.
     * <p>
     * Este teste verifica se a busca de um técnico com um ID existente
     * retorna um status 200 OK e se o corpo da resposta contém os dados
     * do técnico.
     *
     * @throws Exception se ocorrer um erro durante a execução do teste.
     */
    @Test
    public void findByIdDeveriaRetornarTecnicoQuandoIdExistente() throws Exception {
        ResultActions result = mockMvc.perform(get("/tecnicos/{id}", idExistente).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
    }

    /**
     * Testa a busca de um técnico por ID inexistente.
     * <p>
     * Este teste verifica se a busca de um técnico com um ID que não existe
     * retorna um status 404 Not Found.
     *
     * @throws Exception se ocorrer um erro durante a execução do teste.
     */
    @Test
    public void findByIdDeveriaRetornarExceptionNotFoundQuandoIdInexistente() throws Exception {
        ResultActions result = mockMvc.perform(get("/tecnicos/{id}", idInexistente).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
}