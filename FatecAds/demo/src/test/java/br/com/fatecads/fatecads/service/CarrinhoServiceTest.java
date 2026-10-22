package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.demo.DemoApplication;
import br.com.fatecads.fatecads.entity.ItemCarrinho;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.exception.PedidoInvalidoException;
import br.com.fatecads.fatecads.repository.ItemCarrinhoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = DemoApplication.class)
@Transactional
class CarrinhoServiceTest {

    private static final String LOGIN = "comprador";

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private EntityManager entityManager;

    private Produto caderno;

    @BeforeEach
    void configurar() {
        Usuario usuario = new Usuario();
        usuario.setNome("Comprador");
        usuario.setEmail("comprador@email.com");
        usuario.setLogin(LOGIN);
        usuario.setSenha("hash");
        usuarioRepository.save(usuario);

        caderno = new Produto();
        caderno.setDescricao("Caderno");
        caderno.setMarca("Tilibra");
        caderno.setUnidade("UN");
        caderno.setValor(new BigDecimal("12.50"));
        caderno = produtoRepository.save(caderno);
    }

    @Test
    void adicionarMesmoProdutoDuasVezesSomaQuantidadeEmUmItem() {
        carrinhoService.adicionar(LOGIN, caderno.getIdProduto(), 2);
        carrinhoService.adicionar(LOGIN, caderno.getIdProduto(), 3);

        List<ItemCarrinho> itens = carrinhoService.listarItens(LOGIN);
        assertAll(
                () -> assertEquals(1, itens.size()),
                () -> assertEquals(5, itens.get(0).getQuantidade()),
                () -> assertEquals(5, carrinhoService.contarItens(LOGIN)),
                () -> assertEquals(new BigDecimal("62.50"), carrinhoService.calcularTotal(itens))
        );
    }

    @Test
    void quantidadeMenorOuIgualAZeroERejeitada() {
        assertThrows(PedidoInvalidoException.class,
                () -> carrinhoService.adicionar(LOGIN, caderno.getIdProduto(), 0));
        carrinhoService.adicionar(LOGIN, caderno.getIdProduto(), 1);
        assertThrows(PedidoInvalidoException.class,
                () -> carrinhoService.atualizarQuantidade(LOGIN, caderno.getIdProduto(), -1));

        assertEquals(1, carrinhoService.contarItens(LOGIN));
    }

    @Test
    void finalizarCarrinhoVazioFalha() {
        PedidoInvalidoException exception = assertThrows(PedidoInvalidoException.class,
                () -> carrinhoService.finalizar(LOGIN, null));

        assertEquals("Seu carrinho esta vazio", exception.getMessage());
    }

    @Test
    void excluirProdutoQueEstaEmUmCarrinhoRemoveODoCarrinho() {
        carrinhoService.adicionar(LOGIN, caderno.getIdProduto(), 1);
        entityManager.flush();
        entityManager.clear();

        produtoService.excluirPorId(caderno.getIdProduto());
        entityManager.flush();
        entityManager.clear();

        assertTrue(itemCarrinhoRepository.findByUsuarioLoginOrderByIdItemCarrinho(LOGIN).isEmpty());
    }
}
