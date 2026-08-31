package br.com.fatecads.fatecads.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProdutoFrontendTest {

    @Test
    void formularioDevePermitirUploadDeImagem() throws IOException {
        String template = lerRecurso("templates/produtos/formulario.html");

        assertAll(
                () -> assertTrue(template.contains("enctype=\"multipart/form-data\"")),
                () -> assertTrue(template.contains("name=\"imagemArquivo\"")),
                () -> assertTrue(template.contains("accept=\"image/jpeg,image/png,image/webp\"")),
                () -> assertTrue(template.contains("produtoTemImagem"))
        );
    }

    @Test
    void listaDeveExibirImagemSalva() throws IOException {
        String template = lerRecurso("templates/produtos/lista.html");

        assertAll(
                () -> assertTrue(template.contains("produto.temImagem()")),
                () -> assertTrue(template.contains("/produtos/{id}/imagem")),
                () -> assertTrue(template.contains("produto-thumbnail"))
        );
    }

    private String lerRecurso(String caminho) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(caminho)) {
            assertTrue(inputStream != null, "O recurso " + caminho + " deve existir");
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
