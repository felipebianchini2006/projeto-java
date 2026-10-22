package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Item no carrinho persistente do usuario. O carrinho sobrevive a logout/login
 * e vira um {@link Pedido} ao finalizar a compra.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_produto"}))
@Getter
@NoArgsConstructor
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idItemCarrinho;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    // Excluir um produto remove ele dos carrinhos (o historico de pedidos continua protegido)
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    public ItemCarrinho(Usuario usuario, Produto produto, Integer quantidade) {
        this.usuario = usuario;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public void somarQuantidade(Integer quantidade) {
        this.quantidade += quantidade;
    }

    public void definirQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        return produto.getValor()
                .multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
