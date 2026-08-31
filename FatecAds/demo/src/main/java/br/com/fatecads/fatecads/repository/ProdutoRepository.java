package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
}
