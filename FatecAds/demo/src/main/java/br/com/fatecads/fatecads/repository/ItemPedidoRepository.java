package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {

    boolean existsByProdutoIdProduto(Integer idProduto);
}
