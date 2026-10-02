package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LancamentoService {

    private final LancamentoRepository lancamentos;
    private final ModeloMensalRepository modelos;
    private final PagamentoDividaRepository pagamentos;

    public LancamentoService(LancamentoRepository lancamentos, ModeloMensalRepository modelos,
                             PagamentoDividaRepository pagamentos) {
        this.lancamentos = lancamentos;
        this.modelos = modelos;
        this.pagamentos = pagamentos;
    }

    @Transactional(readOnly = true)
    public List<Lancamento> doMes(YearMonth mes) {
        return lancamentos.findByCompetencia(mes.atDay(1));
    }

    @Transactional(readOnly = true)
    public List<PagamentoDivida> pagamentosDeDividaDoMes(YearMonth mes) {
        return pagamentos.findNoPeriodo(mes.atDay(1), mes.atEndOfMonth());
    }

    @Transactional(readOnly = true)
    public ResumoMes resumo(List<Lancamento> doMes, List<PagamentoDivida> pagamentosDivida) {
        BigDecimal entPrev = BigDecimal.ZERO, entReal = BigDecimal.ZERO;
        BigDecimal saiPrev = BigDecimal.ZERO, saiReal = BigDecimal.ZERO;
        int pendentes = 0;
        for (Lancamento l : doMes) {
            if (l.isPendenteDeValor()) {
                pendentes++;
                continue;
            }
            if (l.getTipo() == TipoMovimento.ENTRADA) {
                entPrev = entPrev.add(l.getValor());
                if (l.isEfetivado()) entReal = entReal.add(l.getValor());
            } else {
                saiPrev = saiPrev.add(l.getValor());
                if (l.isEfetivado()) saiReal = saiReal.add(l.getValor());
            }
        }
        for (PagamentoDivida p : pagamentosDivida) {
            saiPrev = saiPrev.add(p.getValor());
            saiReal = saiReal.add(p.getValor());
        }
        return new ResumoMes(entPrev, entReal, saiPrev, saiReal, pendentes);
    }

    public Lancamento salvar(Lancamento form) {
        Lancamento l = form.getId() == null ? new Lancamento() : lancamentos.findById(form.getId()).orElseThrow();
        l.setDescricao(form.getDescricao().trim());
        l.setTipo(form.getTipo());
        l.setNatureza(form.getNatureza());
        l.setValor(form.getValor());
        l.setData(form.getData());
        l.setObservacao(form.getObservacao());
        if (form.isEfetivado()) {
            l.setEfetivado(true);
            l.setDataEfetivacao(form.getDataEfetivacao() != null ? form.getDataEfetivacao() : LocalDate.now());
        } else {
            l.setEfetivado(false);
            l.setDataEfetivacao(null);
        }
        return lancamentos.save(l);
    }

    @Transactional(readOnly = true)
    public Lancamento buscar(Long id) {
        return lancamentos.findById(id).orElseThrow();
    }

    /** Marca/desmarca como pago ou recebido. */
    public Lancamento alternarEfetivado(Long id) {
        Lancamento l = buscar(id);
        l.setEfetivado(!l.isEfetivado());
        l.setDataEfetivacao(l.isEfetivado() ? LocalDate.now() : null);
        return l;
    }

    /** Informa o valor de uma conta variável (ex.: conta de luz do mês). */
    public Lancamento informarValor(Long id, BigDecimal valor) {
        Lancamento l = buscar(id);
        l.setValor(valor);
        return l;
    }

    public void excluir(Long id) {
        lancamentos.deleteById(id);
    }

    /**
     * Cria os lançamentos do mês a partir dos modelos ativos que ainda não foram gerados.
     * Contas variáveis nascem sem valor (ou com a estimativa) para serem preenchidas depois.
     *
     * @return quantidade de lançamentos criados
     */
    public int gerarMes(YearMonth mes) {
        int criados = 0;
        for (ModeloMensal m : modelos.findByAtivoTrue()) {
            if (lancamentos.existsByModeloIdAndCompetencia(m.getId(), mes.atDay(1))) continue;
            Lancamento l = new Lancamento();
            l.setDescricao(m.getDescricao());
            l.setTipo(m.getTipo());
            l.setNatureza(m.getNatureza());
            l.setValor(m.getValor());
            l.setData(mes.atDay(Math.min(m.getDiaVencimento(), mes.lengthOfMonth())));
            l.setModelo(m);
            lancamentos.save(l);
            criados++;
        }
        return criados;
    }
}
