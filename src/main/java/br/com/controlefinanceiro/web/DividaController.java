package br.com.controlefinanceiro.web;

import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.service.DividaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dividas")
public class DividaController {

    private final DividaService service;

    public DividaController(DividaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("dividas", service.listar());
        model.addAttribute("totalEmAberto", service.totalEmAberto());
        return "dividas";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("divida", new Divida());
        return "divida-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("divida", service.buscar(id));
        return "divida-form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("divida") Divida divida, BindingResult result) {
        if (result.hasErrors()) {
            return "divida-form";
        }
        Divida salva = service.salvar(divida);
        return "redirect:/dividas/" + salva.getId();
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("divida", service.buscar(id));
        model.addAttribute("pagamento", new PagamentoDivida());
        return "divida-detalhe";
    }

    @PostMapping("/{id}/pagamentos")
    public String pagar(@PathVariable Long id, @Valid @ModelAttribute("pagamento") PagamentoDivida pagamento,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("divida", service.buscar(id));
            return "divida-detalhe";
        }
        service.pagar(id, pagamento);
        return "redirect:/dividas/" + id;
    }

    @PostMapping("/{id}/pagamentos/{pagamentoId}/excluir")
    public String excluirPagamento(@PathVariable Long id, @PathVariable Long pagamentoId) {
        service.excluirPagamento(id, pagamentoId);
        return "redirect:/dividas/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/dividas";
    }
}
