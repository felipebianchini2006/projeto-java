package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.demo.DemoApplication;
import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.ItemPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ContextConfiguration(classes = DemoApplication.class)
class PedidoRepositoryTest {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void devePersistirPedidoEItensEmCascata() {
        Aluno aluno = alunoRepository.save(criarAluno());
        Produto produto = produtoRepository.save(criarProduto());
        Pedido pedido = new Pedido(aluno, LocalDate.of(2026, 8, 31));
        ItemPedido item = new ItemPedido(pedido, produto, 2, produto.getValor());
        pedido.adicionarItem(item);
        pedido.definirTotal(item.getSubtotal());

        Pedido salvo = pedidoRepository.saveAndFlush(pedido);

        Pedido encontrado = pedidoRepository.findById(salvo.getIdPedido()).orElseThrow();
        assertAll(
                () -> assertNotNull(encontrado.getIdPedido()),
                () -> assertEquals(new BigDecimal("25.00"), encontrado.getTotal()),
                () -> assertEquals(1, encontrado.getItens().size()),
                () -> assertNotNull(encontrado.getItens().get(0).getIdItemPedido()),
                () -> assertEquals(new BigDecimal("12.50"), encontrado.getItens().get(0).getPrecoUnitario())
        );
    }

    private Aluno criarAluno() {
        Aluno aluno = new Aluno();
        aluno.setNomeAluno("Maria");
        aluno.setEmailAluno("maria@email.com");
        aluno.setTelefoneAluno("11999999999");
        aluno.setEnderecoAluno("Rua A");
        aluno.setCpfAluno("12345678900");
        aluno.setRaAluno("2026001");
        return aluno;
    }

    private Produto criarProduto() {
        Produto produto = new Produto();
        produto.setDescricao("Caderno");
        produto.setValor(new BigDecimal("12.50"));
        produto.setUnidade("UN");
        produto.setMarca("Marca A");
        return produto;
    }
}
