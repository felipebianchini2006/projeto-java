package br.com.fatecads.fatecads.entity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProdutoDescontoTest {

    @ParameterizedTest(name = "Preço {0}, desconto {1}%, esperado {2}")
    @CsvSource({
            "100.00,     , 100.00",
            "100.00, 0   , 100.00",
            "100.00, 10  , 90.00",
            "100.00, 25  , 75.00",
            "100.00, 100 , 0.00",
            "19.90,  12.5, 17.41",
            "19.99,  50  , 10.00"
    })
    void calculaPrecoFinalComDuasCasasDecimais(String preco, String percentual, String esperado) {
        Produto produto = new Produto();
        produto.setValor(new BigDecimal(preco));
        produto.setDescontoPercentual(percentual == null ? null : new BigDecimal(percentual));

        assertEquals(new BigDecimal(esperado), produto.getValorFinal());
    }
}
