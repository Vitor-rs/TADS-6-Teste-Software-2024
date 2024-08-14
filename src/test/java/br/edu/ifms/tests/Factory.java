package br.edu.ifms.tests;

import br.edu.ifms.dto.SetorDTO;
import br.edu.ifms.dto.TecnicoDTO;
import br.edu.ifms.entities.Setor;
import br.edu.ifms.entities.Tecnico;

public class Factory {

    public static Tecnico createTecnico() {
        return new Tecnico(1L, "Nando Reis",
                "(67) 99999-8888", "nando@gmail.com", "123456");
    }

    public static TecnicoDTO createTecnicoDTO() {
        Tecnico tecnico = createTecnico();
        return new TecnicoDTO(tecnico);
    }

    public static Setor createSetor() {
        return new Setor(1L, "TI", "Tecnologia da Informação",
                "ti@ifms.edu.br", "(67) 3378-9000", "João Silva");
    }

    public static SetorDTO createSetorDTO() {
        Setor setor = createSetor();
        return new SetorDTO(setor);
    }
}