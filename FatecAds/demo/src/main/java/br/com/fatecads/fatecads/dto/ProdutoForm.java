package br.com.fatecads.fatecads.dto;

import br.com.fatecads.fatecads.entity.Produto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoForm {

    private Integer idProduto;

    @NotBlank(message = "Informe a descricao")
    @Size(max = 120, message = "A descricao deve ter no maximo 120 caracteres")
    private String descricao;

    @NotNull(message = "Informe o valor")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Informe um valor monetario valido")
    private BigDecimal valor;

    @NotBlank(message = "Informe a unidade")
    @Size(max = 20, message = "A unidade deve ter no maximo 20 caracteres")
    private String unidade;

    @NotBlank(message = "Informe a marca")
    @Size(max = 60, message = "A marca deve ter no maximo 60 caracteres")
    private String marca;

    public static ProdutoForm from(Produto produto) {
        return new ProdutoForm(
                produto.getIdProduto(),
                produto.getDescricao(),
                produto.getValor(),
                produto.getUnidade(),
                produto.getMarca()
        );
    }
}
