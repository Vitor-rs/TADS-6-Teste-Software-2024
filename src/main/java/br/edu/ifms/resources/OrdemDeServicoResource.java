package br.edu.ifms.resources;

import br.edu.ifms.dto.OrdemDeServicoDTO;
import br.edu.ifms.services.OrdemDeServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/servicos")
public class OrdemDeServicoResource {

    @Autowired
    private OrdemDeServicoService service;

    @GetMapping
    public ResponseEntity<List<OrdemDeServicoDTO>> findAll() {
        List<OrdemDeServicoDTO> lista = service.findAll();
        return ResponseEntity.ok().body(lista);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<OrdemDeServicoDTO> findById(@PathVariable Long id) {
        OrdemDeServicoDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<OrdemDeServicoDTO> insert(@RequestBody OrdemDeServicoDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<OrdemDeServicoDTO> update(@PathVariable Long id, @RequestBody OrdemDeServicoDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}