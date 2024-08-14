package br.edu.ifms.resources;

import br.edu.ifms.dto.SetorDTO;
import br.edu.ifms.services.SetorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/setores")
public class SetorResource {

    @Autowired
    private SetorService service;

    @GetMapping
    public ResponseEntity<Page<SetorDTO>> findAllPaged(Pageable pageable) {
        Page<SetorDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SetorDTO> findById(@PathVariable Long id) {
        SetorDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<SetorDTO> insert(@RequestBody SetorDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<SetorDTO> update(@PathVariable Long id, @RequestBody SetorDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}