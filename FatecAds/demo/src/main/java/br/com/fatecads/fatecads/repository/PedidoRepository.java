package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}
