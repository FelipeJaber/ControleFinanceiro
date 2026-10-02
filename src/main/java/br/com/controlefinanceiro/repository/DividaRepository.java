package br.com.controlefinanceiro.repository;

import br.com.controlefinanceiro.model.Divida;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DividaRepository extends JpaRepository<Divida, Long> {
    @EntityGraph(attributePaths = "pagamentos")
    List<Divida> findAllByOrderByDataOrigemAscIdAsc();
}
