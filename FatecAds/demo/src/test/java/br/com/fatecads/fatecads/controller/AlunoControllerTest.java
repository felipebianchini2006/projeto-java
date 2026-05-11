package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.service.AlunoService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlunoControllerTest {

    private static final Integer ID_ALUNO = 1;

    @Test
    void listarAlunosDeveRetornarViewDeListaEAdicionarListaAoModel() throws ReflectiveOperationException {
        AlunoController controller = new AlunoController();
        Model model = new ExtendedModelMap();
        Aluno aluno = new Aluno();
        aluno.setIdAluno(ID_ALUNO);
        aluno.setNomeAluno("Maria");
        aluno.setEmailAluno("maria@email.com");
        aluno.setTelefoneAluno("11999999999");
        aluno.setEnderecoAluno("Rua A");
        aluno.setCpfAluno("12345678900");
        aluno.setRaAluno("2024001");

        AlunoService alunoService = new AlunoService() {
            @Override
            public List<Aluno> listarTodos() {
                return List.of(aluno);
            }
        };

        injetarAlunoService(controller, alunoService);

        String viewName = controller.listarAlunos(model);

        assertAll(
                () -> assertEquals("alunos/lista", viewName),
                () -> assertTrue(model.containsAttribute("listaAlunos")),
                () -> assertEquals(List.of(aluno), model.getAttribute("listaAlunos"))
        );
    }

    @Test
    void editarAlunoDeveRetornarFormularioComAlunoExistenteNoModel() throws ReflectiveOperationException {
        AlunoController controller = new AlunoController();
        Model model = new ExtendedModelMap();
        Aluno aluno = criarAlunoPadrao();

        AlunoService alunoService = new AlunoService() {
            @Override
            public Optional<Aluno> buscarPorId(Integer idAluno) {
                return Optional.of(aluno);
            }
        };

        injetarAlunoService(controller, alunoService);

        String viewName = controller.editarAluno(ID_ALUNO, model);

        assertAll(
                () -> assertEquals("alunos/formulario", viewName),
                () -> assertTrue(model.containsAttribute("aluno")),
                () -> assertEquals(aluno, model.getAttribute("aluno"))
        );
    }

    @Test
    void excluirAlunoDeveRemoverAlunoERedirecionarParaLista() throws ReflectiveOperationException {
        AlunoController controller = new AlunoController();
        Integer[] idExcluido = new Integer[1];

        AlunoService alunoService = new AlunoService() {
            @Override
            public void excluirPorId(Integer idAluno) {
                idExcluido[0] = idAluno;
            }
        };

        injetarAlunoService(controller, alunoService);

        String viewName = controller.excluirAluno(ID_ALUNO);

        assertAll(
                () -> assertEquals("redirect:/alunos", viewName),
                () -> assertEquals(ID_ALUNO, idExcluido[0])
        );
    }

    @Test
    void novoAlunoDeveRetornarViewDeFormularioEAdicionarAlunoAoModel() {
        AlunoController controller = new AlunoController();
        Model model = new ExtendedModelMap();

        String viewName = controller.novoAluno(model);

        assertAll(
                () -> assertEquals("alunos/formulario", viewName),
                () -> assertTrue(model.containsAttribute("aluno")),
                () -> assertInstanceOf(Aluno.class, model.getAttribute("aluno"))
        );
    }

    @Test
    void formularioDoAlunoDeveConterCamposBasicos() throws IOException {
        String conteudo = lerTemplate("templates/alunos/formulario.html");

        assertAll(
                () -> assertTrue(conteudo.contains("th:action=\"@{/alunos/salvar}\"")),
                () -> assertTrue(conteudo.contains("th:object=\"${aluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{nomeAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{emailAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{telefoneAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{enderecoAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{cpfAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{raAluno}\""))
        );
    }

    @Test
    void listaDeAlunosDeveConterTabelaELinkDeNovoCadastro() throws IOException {
        String conteudo = lerTemplate("templates/alunos/lista.html");

        assertAll(
                () -> assertTrue(conteudo.contains("th:each=\"aluno : ${listaAlunos}\"")),
                () -> assertTrue(conteudo.contains("th:text=\"${aluno.idAluno}\"")),
                () -> assertTrue(conteudo.contains("th:text=\"${aluno.nomeAluno}\"")),
                () -> assertTrue(conteudo.contains("th:text=\"${aluno.emailAluno}\"")),
                () -> assertTrue(conteudo.contains("th:text=\"${aluno.telefoneAluno}\"")),
                () -> assertTrue(conteudo.contains("th:text=\"${aluno.enderecoAluno}\"")),
                () -> assertTrue(conteudo.contains("th:text=\"${aluno.cpfAluno}\"")),
                () -> assertTrue(conteudo.contains("th:text=\"${aluno.raAluno}\"")),
                () -> assertTrue(conteudo.contains("th:href=\"@{/alunos/novo}\"")),
                () -> assertTrue(conteudo.contains("th:href=\"@{/alunos/editar/{id}(id=${aluno.idAluno})}\"")),
                () -> assertTrue(conteudo.contains("th:action=\"@{/alunos/excluir/{id}(id=${aluno.idAluno})}\"")),
                () -> assertTrue(conteudo.contains("onsubmit=\"return confirm('Deseja excluir este aluno?');\""))
        );
    }

    private void injetarAlunoService(AlunoController controller, AlunoService alunoService)
            throws ReflectiveOperationException {
        var field = AlunoController.class.getDeclaredField("alunoService");
        field.setAccessible(true);
        field.set(controller, alunoService);
    }

    private String lerTemplate(String caminho) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(caminho)) {
            assertTrue(inputStream != null, "O template " + caminho + " deve existir");
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Aluno criarAlunoPadrao() {
        Aluno aluno = new Aluno();
        aluno.setIdAluno(ID_ALUNO);
        aluno.setNomeAluno("Maria");
        aluno.setEmailAluno("maria@email.com");
        aluno.setTelefoneAluno("11999999999");
        aluno.setEnderecoAluno("Rua A");
        aluno.setCpfAluno("12345678900");
        aluno.setRaAluno("2024001");
        return aluno;
    }
}
