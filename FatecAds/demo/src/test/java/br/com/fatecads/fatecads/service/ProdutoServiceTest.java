package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.dto.ProdutoForm;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.exception.ProdutoEmUsoException;
import br.com.fatecads.fatecads.exception.ProdutoNaoEncontradoException;
import br.com.fatecads.fatecads.repository.ItemPedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProdutoServiceTest {

    private ProdutoRepository produtoRepository;
    private ItemPedidoRepository itemPedidoRepository;
    private ProdutoService produtoService;

    @BeforeEach
    void configurar() {
        produtoRepository = mock(ProdutoRepository.class);
        itemPedidoRepository = mock(ItemPedidoRepository.class);
        produtoService = new ProdutoService(produtoRepository, itemPedidoRepository);
    }

    @Test
    void salvarDeveMapearFormularioENormalizarTextos() {
        ProdutoForm form = new ProdutoForm(null, "  Caderno  ", new BigDecimal("19.90"), "  UN ", " Marca A ");
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produto produto = produtoService.salvar(form);

        assertAll(
                () -> assertEquals("Caderno", produto.getDescricao()),
                () -> assertEquals(new BigDecimal("19.90"), produto.getValor()),
                () -> assertEquals("UN", produto.getUnidade()),
                () -> assertEquals("Marca A", produto.getMarca())
        );
        verify(produtoRepository).save(produto);
    }

    @Test
    void buscarObrigatorioDeveFalharQuandoProdutoNaoExiste() {
        when(produtoRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarObrigatorio(10));
    }

    @Test
    void excluirDeveRejeitarProdutoAssociadoAPedido() {
        Produto produto = new Produto();
        produto.setIdProduto(3);
        when(produtoRepository.findById(3)).thenReturn(Optional.of(produto));
        when(itemPedidoRepository.existsByProdutoIdProduto(3)).thenReturn(true);

        assertThrows(ProdutoEmUsoException.class, () -> produtoService.excluirPorId(3));

        verify(produtoRepository, never()).deleteById(3);
    }
}
