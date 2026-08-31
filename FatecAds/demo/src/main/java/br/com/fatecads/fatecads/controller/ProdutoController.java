package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.dto.ProdutoForm;
import br.com.fatecads.fatecads.exception.ProdutoEmUsoException;
import br.com.fatecads.fatecads.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping({"", "/listar"})
    public String listar(Model model) {
        model.addAttribute("listaProdutos", produtoService.listarTodos());
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new ProdutoForm());
        return "produtos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer idProduto, Model model) {
        model.addAttribute("produto", produtoService.buscarFormulario(idProduto));
        return "produtos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("produto") ProdutoForm produto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "produtos/formulario";
        }
        produtoService.salvar(produto);
        return "redirect:/produtos";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(
            @PathVariable("id") Integer idProduto,
            RedirectAttributes redirectAttributes
    ) {
        try {
            produtoService.excluirPorId(idProduto);
        } catch (ProdutoEmUsoException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/produtos";
    }
}
