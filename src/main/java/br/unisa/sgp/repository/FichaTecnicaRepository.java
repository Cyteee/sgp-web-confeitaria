package br.unisa.sgp.repository;

import br.unisa.sgp.model.FichaTecnica;
import br.unisa.sgp.model.FichaTecnicaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FichaTecnicaRepository extends JpaRepository<FichaTecnica, FichaTecnicaId> {

    boolean existsByInsumoId(Long insumoId);
}
