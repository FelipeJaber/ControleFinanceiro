package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.model.Divida;
import br.com.controlefinanceiro.model.PagamentoDivida;
import br.com.controlefinanceiro.repository.DividaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DividaService {

    private final DividaRepository dividas;

    public DividaService(DividaRepository dividas) {
        this.dividas = dividas;
    }

    @Transactional(readOnly = true)
    public List<Divida> listar() {
        return dividas.findAllByOrderByDataOrigemAscIdAsc();
    }

    @Transactional(readOnly = true)
    public Divida buscar(Long id) {
        Divida d = dividas.findById(id).orElseThrow();
        d.getPagamentos().size(); // inicializa a coleção
        return d;
    }

    /** Soma dos saldos de todas as dívidas ainda em aberto. */
    @Transactional(readOnly = true)
    public BigDecimal totalEmAberto() {
        return dividas.findAll().stream()
                .map(Divida::getSaldo)
                .filter(s -> s.signum() > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Divida salvar(Divida form) {
        Divida d = form.getId() == null ? new Divida() : dividas.findById(form.getId()).orElseThrow();
        d.setDescricao(form.getDescricao().trim());
        d.setCredor(form.getCredor());
        d.setValorOriginal(form.getValorOriginal());
        d.setDataOrigem(form.getDataOrigem());
        d.setObservacao(form.getObservacao());
        return dividas.save(d);
    }

    public void pagar(Long dividaId, PagamentoDivida form) {
        Divida d = dividas.findById(dividaId).orElseThrow();
        PagamentoDivida p = new PagamentoDivida();
        p.setDivida(d);
        p.setData(form.getData());
        p.setValor(form.getValor());
        p.setObservacao(form.getObservacao());
        d.getPagamentos().add(p);
    }

    public void excluirPagamento(Long dividaId, Long pagamentoId) {
        dividas.findById(dividaId).orElseThrow().getPagamentos().removeIf(p -> p.getId().equals(pagamentoId));
    }

    public void excluir(Long id) {
        dividas.deleteById(id);
    }
}
