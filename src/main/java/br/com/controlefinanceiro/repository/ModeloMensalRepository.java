package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.ModeloMensal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloMensalRepository extends JpaRepository<ModeloMensal, Long> {
    List<ModeloMensal> findByAtivoTrue();
    List<ModeloMensal> findAllByOrderByTipoAscDescricaoAsc();
}
