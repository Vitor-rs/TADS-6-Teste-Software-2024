package br.edu.ifms.repositories;

import br.edu.ifms.entities.OrdemDeServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemDeServicoRepository extends JpaRepository<OrdemDeServico, Long> {

}