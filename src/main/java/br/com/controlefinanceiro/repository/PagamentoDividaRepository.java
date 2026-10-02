package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.PagamentoDivida;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PagamentoDividaRepository extends JpaRepository<PagamentoDivida, Long> {

    @Query("select p from PagamentoDivida p join fetch p.divida where p.data >= :inicio and p.data <= :fim order by p.data, p.id")
    List<PagamentoDivida> findNoPeriodo(LocalDate inicio, LocalDate fim);
}
