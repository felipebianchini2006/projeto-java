package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idProduto;

    @Column(nullable = false, length = 120)
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(precision = 5, scale = 2)
    private BigDecimal descontoPercentual;

    @Column(nullable = false, length = 20)
    private String unidade;

    @Column(nullable = false, length = 60)
    private String marca;

    @Lob
    private byte[] imagem;

    @Column(length = 30)
    private String imagemTipo;

    public BigDecimal getValorFinal() {
        BigDecimal desconto = descontoPercentual == null ? BigDecimal.ZERO : descontoPercentual;
        return valor.multiply(BigDecimal.ONE.subtract(desconto.movePointLeft(2)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean temImagem() {
        return imagem != null && imagem.length > 0;
    }
}
