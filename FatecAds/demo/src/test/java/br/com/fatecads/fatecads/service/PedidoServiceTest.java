package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.dto.CriarPedidoRequest;
import br.com.fatecads.fatecads.dto.ItemPedidoRequest;
import br.com.fatecads.fatecads.dto.PedidoResponse;
import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.exception.PedidoInvalidoException;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PedidoServiceTest {

    private PedidoRepository pedidoRepository;
    private AlunoService alunoService;
    private ProdutoService produtoService;
    private PedidoService pedidoService;

    @BeforeEach
    void configurar() {
        pedidoRepository = mock(PedidoRepository.class);
        alunoService = mock(AlunoService.class);
        produtoService = mock(ProdutoService.class);
        pedidoService = new PedidoService(pedidoRepository, alunoService, produtoService);
    }

    @Test
    void criarDeveCalcularPrecoSubtotalETotalNoServidor() {
        Aluno aluno = criarAluno(1);
        Produto produto = criarProduto(2, "9.99");
        CriarPedidoRequest request = new CriarPedidoRequest(
                aluno.getIdAluno(),
                List.of(new ItemPedidoRequest(produto.getIdProduto(), 3))
        );
        when(alunoService.buscarObrigatorio(1)).thenReturn(aluno);
        when(produtoService.buscarObrigatorio(2)).thenReturn(produto);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoResponse response = pedidoService.criar(request);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        Pedido pedido = captor.getValue();

        assertAll(
                () -> assertSame(aluno, pedido.getAluno()),
                () -> assertEquals(new BigDecimal("29.97"), pedido.getTotal()),
                () -> assertEquals(1, pedido.getItens().size()),
                () -> assertSame(produto, pedido.getItens().get(0).getProduto()),
                () -> assertEquals(new BigDecimal("9.99"), pedido.getItens().get(0).getPrecoUnitario()),
                () -> assertEquals(new BigDecimal("29.97"), pedido.getItens().get(0).getSubtotal()),
                () -> assertSame(pedido, pedido.getItens().get(0).getPedido()),
                () -> assertEquals(new BigDecimal("29.97"), response.total())
        );
    }

    @Test
    void criarDeveRejeitarPedidoSemItens() {
        CriarPedidoRequest request = new CriarPedidoRequest(1, List.of());

        PedidoInvalidoException exception = assertThrows(
                PedidoInvalidoException.class,
                () -> pedidoService.criar(request)
        );

        assertEquals("Adicione pelo menos um item ao pedido", exception.getMessage());
        verifyNoInteractions(pedidoRepository, alunoService, produtoService);
    }

    @Test
    void criarDeveRejeitarQuantidadeInvalidaAntesDeBuscarProduto() {
        Aluno aluno = criarAluno(1);
        when(alunoService.buscarObrigatorio(1)).thenReturn(aluno);
        CriarPedidoRequest request = new CriarPedidoRequest(
                1,
                List.of(new ItemPedidoRequest(2, 0))
        );

        PedidoInvalidoException exception = assertThrows(
                PedidoInvalidoException.class,
                () -> pedidoService.criar(request)
        );

        assertEquals("A quantidade deve ser maior que zero", exception.getMessage());
        verifyNoInteractions(produtoService, pedidoRepository);
    }

    private Aluno criarAluno(Integer idAluno) {
        Aluno aluno = new Aluno();
        aluno.setIdAluno(idAluno);
        aluno.setNomeAluno("Maria");
        return aluno;
    }

    private Produto criarProduto(Integer idProduto, String valor) {
        Produto produto = new Produto();
        produto.setIdProduto(idProduto);
        produto.setDescricao("Caderno");
        produto.setValor(new BigDecimal(valor));
        produto.setUnidade("UN");
        produto.setMarca("Marca A");
        return produto;
    }
}
