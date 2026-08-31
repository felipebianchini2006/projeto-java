package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.dto.ProdutoForm;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.exception.ImagemProdutoInvalidaException;
import br.com.fatecads.fatecads.exception.ProdutoEmUsoException;
import br.com.fatecads.fatecads.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
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
        model.addAttribute("produtoTemImagem", false);
        return "produtos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer idProduto, Model model) {
        Produto produto = produtoService.buscarObrigatorio(idProduto);
        model.addAttribute("produto", ProdutoForm.from(produto));
        model.addAttribute("produtoTemImagem", produto.temImagem());
        return "produtos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("produto") ProdutoForm produto,
            BindingResult bindingResult,
            @RequestParam("imagemArquivo") MultipartFile imagemArquivo,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            adicionarImagemAtualAoModel(produto, model);
            return "produtos/formulario";
        }
        try {
            produtoService.salvar(produto, imagemArquivo);
        } catch (ImagemProdutoInvalidaException exception) {
            model.addAttribute("erroImagem", exception.getMessage());
            adicionarImagemAtualAoModel(produto, model);
            return "produtos/formulario";
        }
        return "redirect:/produtos";
    }

    @GetMapping("/{id}/imagem")
    @ResponseBody
    public ResponseEntity<byte[]> imagem(@PathVariable("id") Integer idProduto) {
        Produto produto = produtoService.buscarObrigatorio(idProduto);
        if (!produto.temImagem()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(produto.getImagemTipo()))
                .body(produto.getImagem());
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

    private void adicionarImagemAtualAoModel(ProdutoForm produto, Model model) {
        boolean temImagem = produto.getIdProduto() != null
                && produtoService.buscarObrigatorio(produto.getIdProduto()).temImagem();
        model.addAttribute("produtoTemImagem", temImagem);
    }
}
