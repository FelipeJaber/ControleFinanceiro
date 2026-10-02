package br.com.controlefinanceiro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.repository.*;
import br.com.controlefinanceiro.service.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:teste;DB_CLOSE_DELAY=-1")
class FluxoCompletoTest {

    @Autowired MockMvc mvc;
    @Autowired LancamentoService lancamentos;
    @Autowired ModeloMensalService modelos;
    @Autowired DividaService dividas;

    @Test
    void geraMesComFixasEVariaveisECalculaResumo() throws Exception {
        mvc.perform(post("/modelos").param("tipo", "ENTRADA").param("natureza", "FIXO")
                        .param("descricao", "Salário").param("valor", "5000").param("diaVencimento", "5").param("ativo", "true"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/modelos").param("tipo", "SAIDA").param("natureza", "FIXO")
                        .param("descricao", "Aluguel").param("valor", "1500").param("diaVencimento", "31").param("ativo", "true"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/modelos").param("tipo", "SAIDA").param("natureza", "VARIAVEL")
                        .param("descricao", "Luz").param("diaVencimento", "10").param("ativo", "true"))
                .andExpect(status().is3xxRedirection());
        // fixa sem valor é rejeitada
        mvc.perform(post("/modelos").param("tipo", "SAIDA").param("natureza", "FIXO")
                        .param("descricao", "Sem valor").param("diaVencimento", "1"))
                .andExpect(status().isOk()).andExpect(view().name("modelo-form"));

        YearMonth fev = YearMonth.of(2027, 2);
        assertThat(lancamentos.gerarMes(fev)).isEqualTo(3);
        assertThat(lancamentos.gerarMes(fev)).as("idempotente").isZero();

        List<Lancamento> doMes = lancamentos.doMes(fev);
        Lancamento aluguel = doMes.stream().filter(l -> l.getDescricao().equals("Aluguel")).findFirst().orElseThrow();
        assertThat(aluguel.getData().getDayOfMonth()).as("dia 31 limitado ao fim de fevereiro").isEqualTo(28);
        Lancamento luz = doMes.stream().filter(l -> l.getDescricao().equals("Luz")).findFirst().orElseThrow();
        assertThat(luz.isPendenteDeValor()).isTrue();

        ResumoMes antes = lancamentos.resumo(doMes, List.of());
        assertThat(antes.pendentesDeValor()).isEqualTo(1);
        assertThat(antes.saldoPrevisto()).isEqualByComparingTo("3500");

        mvc.perform(post("/lancamentos/" + luz.getId() + "/valor").param("valor", "230.50"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/lancamentos/" + aluguel.getId() + "/efetivar")).andExpect(status().is3xxRedirection());

        ResumoMes depois = lancamentos.resumo(lancamentos.doMes(fev), List.of());
        assertThat(depois.pendentesDeValor()).isZero();
        assertThat(depois.saidasPrevistas()).isEqualByComparingTo("1730.50");
        assertThat(depois.saidasRealizadas()).isEqualByComparingTo("1500");

        mvc.perform(get("/").param("mes", "2027-02")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Fevereiro de 2027")));
    }

    @Test
    void lancamentoEsporadicoEDividaComPagamentoParcial() throws Exception {
        mvc.perform(post("/lancamentos").param("tipo", "ENTRADA").param("natureza", "ESPORADICO")
                        .param("descricao", "Freela").param("valor", "800").param("data", "2027-03-15"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/?mes=2027-03"));
        mvc.perform(post("/lancamentos").param("tipo", "SAIDA").param("natureza", "ESPORADICO")
                        .param("descricao", "").param("data", "2027-03-15"))
                .andExpect(status().isOk()).andExpect(view().name("lancamento-form"));

        Divida d = new Divida();
        d.setDescricao("Cartão antigo");
        d.setValorOriginal(new BigDecimal("1000"));
        Long id = dividas.salvar(d).getId();
        BigDecimal antes = dividas.totalEmAberto();

        mvc.perform(post("/dividas/" + id + "/pagamentos").param("data", "2027-03-20").param("valor", "300"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/dividas/" + id + "/pagamentos").param("data", "2027-03-20").param("valor", "-5"))
                .andExpect(status().isOk()).andExpect(view().name("divida-detalhe"));

        assertThat(dividas.buscar(id).getSaldo()).isEqualByComparingTo("700");
        assertThat(dividas.totalEmAberto()).isEqualByComparingTo(antes.subtract(new BigDecimal("300")));

        YearMonth mar = YearMonth.of(2027, 3);
        ResumoMes r = lancamentos.resumo(lancamentos.doMes(mar), lancamentos.pagamentosDeDividaDoMes(mar));
        assertThat(r.saidasRealizadas()).isEqualByComparingTo("300");

        mvc.perform(get("/dividas")).andExpect(status().isOk());
        mvc.perform(get("/dividas/" + id)).andExpect(status().isOk());
        mvc.perform(get("/modelos")).andExpect(status().isOk());
        mvc.perform(get("/lancamentos/novo")).andExpect(status().isOk());
    }
}
