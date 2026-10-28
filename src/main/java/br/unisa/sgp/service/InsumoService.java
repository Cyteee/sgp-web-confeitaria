package br.unisa.sgp.service;

import br.unisa.sgp.exception.RegraDeNegocioException;
import br.unisa.sgp.model.Insumo;
import br.unisa.sgp.repository.FichaTecnicaRepository;
import br.unisa.sgp.repository.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;

    public InsumoService(InsumoRepository insumoRepository, FichaTecnicaRepository fichaTecnicaRepository) {
        this.insumoRepository = insumoRepository;
        this.fichaTecnicaRepository = fichaTecnicaRepository;
    }

    public List<Insumo> listar() {
        return insumoRepository.findAll();
    }

    public List<Insumo> listarAbaixoDoMinimo() {
        return insumoRepository.findAbaixoDoMinimo();
    }

    @Transactional
    public Insumo salvar(Insumo insumo) {
        return insumoRepository.save(insumo);
    }

    @Transactional
    public void excluir(Long id) {
        if (fichaTecnicaRepository.existsByInsumoId(id)) {
            throw new RegraDeNegocioException("Insumo vinculado a uma ficha técnica não pode ser excluído: " + id);
        }
        insumoRepository.deleteById(id);
    }
}
