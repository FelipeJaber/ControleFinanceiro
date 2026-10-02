package br.com.controlefinanceiro.web;

import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.service.*;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    private final LancamentoService lancamentos;
    private final DividaService dividas;

    public DashboardController(LancamentoService lancamentos, DividaService dividas) {
        this.lancamentos = lancamentos;
        this.dividas = dividas;
    }

    @GetMapping("/")
    public String mes(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes, Model model) {
        YearMonth m = MesUtil.ou(mes);
        List<Lancamento> doMes = lancamentos.doMes(m);
        List<PagamentoDivida> pagDividas = lancamentos.pagamentosDeDividaDoMes(m);

        Map<Natureza, List<Lancamento>> porNatureza = new EnumMap<>(Natureza.class);
        for (Natureza n : Natureza.values()) {
            porNatureza.put(n, doMes.stream().filter(l -> l.getNatureza() == n).toList());
        }

        model.addAttribute("mes", m);
        model.addAttribute("rotuloMes", MesUtil.rotulo(m));
        model.addAttribute("anterior", m.minusMonths(1));
        model.addAttribute("proximo", m.plusMonths(1));
        model.addAttribute("porNatureza", porNatureza);
        model.addAttribute("pagamentosDivida", pagDividas);
        model.addAttribute("resumo", lancamentos.resumo(doMes, pagDividas));
        model.addAttribute("dividaEmAberto", dividas.totalEmAberto());
        model.addAttribute("naturezas", Natureza.values());
        return "dashboard";
    }

    @PostMapping("/gerar")
    public String gerar(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes, RedirectAttributes ra) {
        int criados = lancamentos.gerarMes(mes);
        ra.addFlashAttribute("aviso", criados == 0
                ? "Nada novo a gerar: todos os modelos ativos já estão neste mês."
                : criados + " lançamento(s) gerado(s) a partir dos modelos mensais.");
        return "redirect:/?mes=" + mes;
    }
}
