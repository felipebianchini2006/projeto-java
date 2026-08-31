package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.dto.CriarPedidoRequest;
import br.com.fatecads.fatecads.dto.PedidoResponse;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.PedidoService;
import br.com.fatecads.fatecads.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final AlunoService alunoService;
    private final ProdutoService produtoService;

    public PedidoController(
            PedidoService pedidoService,
            AlunoService alunoService,
            ProdutoService produtoService
    ) {
        this.pedidoService = pedidoService;
        this.alunoService = alunoService;
        this.produtoService = produtoService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("listaAlunos", alunoService.listarTodos());
        model.addAttribute("listaProdutos", produtoService.listarTodos());
        return "pedidos/formulario";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody CriarPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(request));
    }
}
