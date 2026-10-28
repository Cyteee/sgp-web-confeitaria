package br.unisa.sgp.repository;

import br.unisa.sgp.model.Insumo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Insumo i where i.id in :ids")
    List<Insumo> findAllByIdForUpdate(Collection<Long> ids);

    @Query("select i from Insumo i where i.quantidadeEstoque <= i.estoqueMinimo")
    List<Insumo> findAbaixoDoMinimo();
}
