package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.ItemCarrinho;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Integer> {

    @EntityGraph(attributePaths = "produto")
    List<ItemCarrinho> findByUsuarioLoginOrderByIdItemCarrinho(String login);

    Optional<ItemCarrinho> findByUsuarioLoginAndProdutoIdProduto(String login, Integer idProduto);

    @Query("select coalesce(sum(item.quantidade), 0) from ItemCarrinho item where item.usuario.login = :login")
    long somarQuantidadePorLogin(@Param("login") String login);
}
