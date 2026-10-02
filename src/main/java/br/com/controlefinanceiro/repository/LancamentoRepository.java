package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.Lancamento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query("select l from Lancamento l left join fetch l.modelo where l.competencia = :competencia order by l.data, l.id")
    List<Lancamento> findByCompetencia(LocalDate competencia);

    boolean existsByModeloIdAndCompetencia(Long modeloId, LocalDate competencia);

    @Modifying
    @Query("update Lancamento l set l.modelo = null where l.modelo.id = :modeloId")
    void desvincularModelo(Long modeloId);
}
