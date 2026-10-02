package br.com.controlefinanceiro.service;

import br.com.controlefinanceiro.model.ModeloMensal;
import br.com.controlefinanceiro.repository.LancamentoRepository;
import br.com.controlefinanceiro.repository.ModeloMensalRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModeloMensalService {

    private final ModeloMensalRepository modelos;
    private final LancamentoRepository lancamentos;

    public ModeloMensalService(ModeloMensalRepository modelos, LancamentoRepository lancamentos) {
        this.modelos = modelos;
        this.lancamentos = lancamentos;
    }

    @Transactional(readOnly = true)
    public List<ModeloMensal> listar() {
        return modelos.findAllByOrderByTipoAscDescricaoAsc();
    }

    @Transactional(readOnly = true)
    public ModeloMensal buscar(Long id) {
        return modelos.findById(id).orElseThrow();
    }

    public void salvar(ModeloMensal form) {
        ModeloMensal m = form.getId() == null ? new ModeloMensal() : buscar(form.getId());
        m.setDescricao(form.getDescricao().trim());
        m.setTipo(form.getTipo());
        m.setNatureza(form.getNatureza());
        m.setValor(form.getValor());
        m.setDiaVencimento(form.getDiaVencimento());
        m.setAtivo(form.isAtivo());
        modelos.save(m);
    }

    public void alternarAtivo(Long id) {
        ModeloMensal m = buscar(id);
        m.setAtivo(!m.isAtivo());
    }

    /** Remove o modelo; lançamentos já gerados são mantidos. */
    public void excluir(Long id) {
        lancamentos.desvincularModelo(id);
        modelos.deleteById(id);
    }
}
