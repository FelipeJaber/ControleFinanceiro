package br.com.controlefinanceiro.web;

import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.service.LancamentoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lancamentos")
public class LancamentoController {

    private final LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(defaultValue = "SAIDA") TipoMovimento tipo,
                       @RequestParam(defaultValue = "ESPORADICO") Natureza natureza,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes,
                       Model model) {
        Lancamento l = new Lancamento();
        l.setTipo(tipo);
        l.setNatureza(natureza);
        YearMonth m = MesUtil.ou(mes);
        l.setData(m.equals(YearMonth.now()) ? LocalDate.now() : m.atDay(1));
        return formulario(l, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(service.buscar(id), model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("lancamento") Lancamento lancamento, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return formulario(lancamento, model);
        }
        Lancamento salvo = service.salvar(lancamento);
        return "redirect:/?mes=" + YearMonth.from(salvo.getData());
    }

    @PostMapping("/{id}/efetivar")
    public String efetivar(@PathVariable Long id) {
        Lancamento l = service.alternarEfetivado(id);
        return "redirect:/?mes=" + YearMonth.from(l.getData());
    }

    @PostMapping("/{id}/valor")
    public String informarValor(@PathVariable Long id, @RequestParam BigDecimal valor) {
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("O valor não pode ser negativo");
        }
        Lancamento l = service.informarValor(id, valor);
        return "redirect:/?mes=" + YearMonth.from(l.getData());
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        YearMonth mes = YearMonth.from(service.buscar(id).getData());
        service.excluir(id);
        return "redirect:/?mes=" + mes;
    }

    private String formulario(Lancamento l, Model model) {
        model.addAttribute("lancamento", l);
        model.addAttribute("tipos", TipoMovimento.values());
        model.addAttribute("naturezas", Natureza.values());
        return "lancamento-form";
    }
}
