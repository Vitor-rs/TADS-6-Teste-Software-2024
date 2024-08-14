package br.edu.ifms.resources;

import br.edu.ifms.dto.TecnicoDTO;
import br.edu.ifms.services.TecnicoService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classe de testes para o recurso TecnicoResource.
 */
@WebMvcTest(TecnicoResource.class)
public class TecnicoResourceTests {

    // Injeta o MockMvc para simular requisições HTTP
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TecnicoService service;

    // Injeta o ObjectMapper para converter objetos em JSON
    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private TecnicoDTO tecnicoDTO;
    private PageImpl<TecnicoDTO> page;

    /**
     * Configuração inicial dos testes.
     *
     * @throws Exception se ocorrer algum erro durante a configuração
     */
    @BeforeEach
    public void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        tecnicoDTO = Factory.createTecnicoDTO();
        page = new PageImpl<>(List.of(tecnicoDTO));

        when(service.findAllPaged(any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(tecnicoDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any())).thenReturn(tecnicoDTO);

        when(service.update(eq(existingId), any())).thenReturn(tecnicoDTO);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
    }

    /**
     * Testa se o método findAllPaged retorna uma página de TecnicoDTO.
     *
     * @throws Exception se ocorrer algum erro durante a execução do teste
     */
    @Test
    public void findAllPagedDeveriaRetornarPagina() throws Exception {
        mockMvc.perform(get("/tecnicos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Testa se o método findById retorna TecnicoDTO quando o ID é existente.
     *
     * @throws Exception se ocorrer algum erro durante a execução do teste
     */
    @Test
    public void findByIdDeveriaRetornarTecnicoQuandoIdExistir() throws Exception {
        mockMvc.perform(get("/tecnicos/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").exists());
    }

    /**
     * Testa se o método findById retorna NotFound quando o ID não existe.
     *
     * @throws Exception se ocorrer algum erro durante a execução do teste
     */
    @Test
    public void findByIdDeveriaRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        // O mockMvc não aceita exceções, então é necessário verificar o status da resposta
        mockMvc.perform(get("/tecnicos/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Testa se o método insert retorna TecnicoDTO criado.
     *
     * @throws Exception se ocorrer algum erro durante a execução do teste
     */
    @Test
    public void insertDeveriaRetornarTecnicoCriado() throws Exception {
        // Converte o objeto em JSON
        String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

        // Faz a requisição HTTP
        mockMvc.perform(post("/tecnicos")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").exists());
    }

    /**
     * Testa se o método update retorna TecnicoDTO atualizado quando o ID é existente.
     *
     * @throws Exception se ocorrer algum erro durante a execução do teste
     */
    @Test
    public void updateDeveriaRetornarTecnicoQuandoIdExistir() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

        mockMvc.perform(put("/tecnicos/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").exists());
    }

    /**
     * Testa se o método update retorna NotFound quando o ID não existe.
     *
     * @throws Exception se ocorrer algum erro durante a execução do teste
     */
    @Test
    public void updateDeveriaRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(tecnicoDTO);

        mockMvc.perform(put("/tecnicos/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Testa se o método delete retorna NoContent quando o ID é existente.
     *
     * @throws Exception se ocorrer algum erro durante a execução do teste
     */
    @Test
    public void deleteDeveriaRetornarNoContentQuandoIdExistir() throws Exception {
        // O mockMvc não aceita métodos DELETE com corpo, então não é necessário enviar um JSON
        mockMvc.perform(delete("/tecnicos/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /**
     * Testa se o método delete retorna NotFound quando o ID não existe.
     *
     * @throws Exception se ocorrer algum erro durante a execução do teste
     */
    @Test
    public void deleteDeveriaRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        mockMvc.perform(delete("/tecnicos/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}