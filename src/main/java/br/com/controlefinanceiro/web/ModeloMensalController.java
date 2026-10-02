package br.com.controlefinanceiro.web;

import br.com.controlefinanceiro.model.*;
import br.com.controlefinanceiro.service.ModeloMensalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/modelos")
public class ModeloMensalController {

    private final ModeloMensalService service;

    public ModeloMensalController(ModeloMensalService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("modelos", service.listar());
        return "modelos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new ModeloMensal(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(service.buscar(id), model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("modelo") ModeloMensal modelo, BindingResult result, Model model) {
        if (modelo.getNatureza() == Natureza.ESPORADICO) {
            result.rejectValue("natureza", "invalida", "Modelos mensais são fixos ou variáveis");
        }
        if (modelo.getNatureza() == Natureza.FIXO && modelo.getValor() == null) {
            result.rejectValue("valor", "obrigatorio", "Informe o valor da conta fixa");
        }
        if (result.hasErrors()) {
            return formulario(modelo, model);
        }
        service.salvar(modelo);
        return "redirect:/modelos";
    }

    @PostMapping("/{id}/ativo")
    public String alternarAtivo(@PathVariable Long id) {
        service.alternarAtivo(id);
        return "redirect:/modelos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/modelos";
    }

    private String formulario(ModeloMensal m, Model model) {
        model.addAttribute("modelo", m);
        model.addAttribute("tipos", TipoMovimento.values());
        model.addAttribute("naturezas", List.of(Natureza.FIXO, Natureza.VARIAVEL));
        return "modelo-form";
    }
}
