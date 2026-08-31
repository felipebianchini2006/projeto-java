package br.com.fatecads.fatecads.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedidoFrontendTest {

    @Test
    void formularioDeveDeclararCsrfEUsarJavaScriptExterno() throws IOException {
        String template = lerRecurso("templates/pedidos/formulario.html");

        assertAll(
                () -> assertTrue(template.contains("name=\"_csrf\"")),
                () -> assertTrue(template.contains("name=\"_csrf_header\"")),
                () -> assertTrue(template.contains("th:src=\"@{/js/pedidos.js}\"")),
                () -> assertTrue(template.contains("th:each=\"aluno : ${listaAlunos}\"")),
                () -> assertTrue(template.contains("th:each=\"produto : ${listaProdutos}\""))
        );
    }

    @Test
    void scriptDeveEnviarCsrfESomenteIdsEQuantidade() throws IOException {
        String script = lerRecurso("static/js/pedidos.js");

        assertAll(
                () -> assertTrue(script.contains("[csrfHeader]: csrfToken")),
                () -> assertTrue(script.contains("produtoId: item.produtoId")),
                () -> assertTrue(script.contains("quantidade: item.quantidade")),
                () -> assertTrue(script.contains("quantidade.addEventListener(\"input\"")),
                () -> assertTrue(script.contains("subtotalCell.textContent")),
                () -> assertTrue(script.contains("atualizarTotal()")),
                () -> assertTrue(script.contains("fetch(\"/pedidos\"")),
                () -> assertTrue(script.contains("method: \"POST\""))
        );
    }

    private String lerRecurso(String caminho) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(caminho)) {
            assertTrue(inputStream != null, "O recurso " + caminho + " deve existir");
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
