package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @EntityGraph(attributePaths = {"usuario", "aluno", "itens", "itens.produto"})
    List<Pedido> findByUsuarioLoginOrderByIdPedidoDesc(String login);

    @EntityGraph(attributePaths = {"usuario", "aluno", "itens", "itens.produto"})
    List<Pedido> findAllByOrderByIdPedidoDesc();
}
