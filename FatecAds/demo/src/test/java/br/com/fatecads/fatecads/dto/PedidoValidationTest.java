package br.com.fatecads.fatecads.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void configurarValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void fecharValidator() {
        validatorFactory.close();
    }

    @Test
    void deveValidarAlunoItensEQuantidade() {
        CriarPedidoRequest request = new CriarPedidoRequest(
                null,
                List.of(new ItemPedidoRequest(null, 0))
        );

        var mensagens = validator.validate(request).stream()
                .map(violacao -> violacao.getMessage())
                .sorted()
                .toList();

        assertEquals(
                List.of("A quantidade deve ser maior que zero", "Informe o aluno", "Informe o produto"),
                mensagens
        );
    }
}
