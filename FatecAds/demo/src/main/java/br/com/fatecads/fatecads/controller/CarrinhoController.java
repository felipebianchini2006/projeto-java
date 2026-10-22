package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.ItemCarrinho;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.exception.PedidoInvalidoException;
import br.com.fatecads.fatecads.exception.ProdutoNaoEncontradoException;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.CarrinhoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final AlunoService alunoService;

    public CarrinhoController(CarrinhoService carrinhoService, AlunoService alunoService) {
        this.carrinhoService = carrinhoService;
        this.alunoService = alunoService;
    }

    @GetMapping
    public String ver(Model model, Principal principal, HttpServletRequest request) {
        List<ItemCarrinho> itens = carrinhoService.listarItens(principal.getName());
        model.addAttribute("itens", itens);
        model.addAttribute("total", carrinhoService.calcularTotal(itens));
        if (request.isUserInRole("ADMIN")) {
            model.addAttribute("listaAlunos", alunoService.listarTodos());
        }
        return "carrinho/carrinho";
    }

    @PostMapping("/adicionar")
    public String adicionar(
            @RequestParam Integer produtoId,
            @RequestParam(required = false) Integer quantidade,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ItemCarrinho item = carrinhoService.adicionar(principal.getName(), produtoId, quantidade);
            redirectAttributes.addFlashAttribute("mensagem",
                    item.getProduto().getDescricao() + " adicionado ao carrinho.");
        } catch (PedidoInvalidoException | ProdutoNaoEncontradoException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/produtos";
    }

    @PostMapping("/atualizar")
    public String atualizar(
            @RequestParam Integer produtoId,
            @RequestParam(required = false) Integer quantidade,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            carrinhoService.atualizarQuantidade(principal.getName(), produtoId, quantidade);
        } catch (PedidoInvalidoException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/remover")
    public String remover(@RequestParam Integer produtoId, Principal principal) {
        carrinhoService.remover(principal.getName(), produtoId);
        return "redirect:/carrinho";
    }

    @PostMapping("/finalizar")
    public String finalizar(
            @RequestParam(required = false) Integer alunoId,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Pedido pedido = carrinhoService.finalizar(principal.getName(), alunoId);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Compra #" + pedido.getIdPedido() + " finalizada com sucesso!");
            return "redirect:/pedidos";
        } catch (PedidoInvalidoException | ProdutoNaoEncontradoException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/carrinho";
        }
    }
}
