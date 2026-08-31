package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.CursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlunoControllerTest {

    private static final Integer ID_ALUNO = 1;
    private static final Integer ID_CURSO = 2;

    private AlunoService alunoService;
    private CursoService cursoService;
    private AlunoController controller;

    @BeforeEach
    void configurar() {
        alunoService = mock(AlunoService.class);
        cursoService = mock(CursoService.class);
        controller = new AlunoController(alunoService, cursoService);
    }

    @Test
    void listarAlunosDeveRetornarViewDeListaEAdicionarListaAoModel() {
        Model model = new ExtendedModelMap();
        Aluno aluno = criarAlunoPadrao();
        when(alunoService.listarTodos()).thenReturn(List.of(aluno));

        String viewName = controller.listarAlunos(model);

        assertAll(
                () -> assertEquals("alunos/lista", viewName),
                () -> assertEquals(List.of(aluno), model.getAttribute("listaAlunos"))
        );
    }

    @Test
    void editarAlunoDeveRetornarFormularioComAlunoECursosNoModel() {
        Model model = new ExtendedModelMap();
        Aluno aluno = criarAlunoPadrao();
        Curso curso = criarCurso();
        when(alunoService.buscarObrigatorio(ID_ALUNO)).thenReturn(aluno);
        when(cursoService.listarTodos()).thenReturn(List.of(curso));

        String viewName = controller.editarAluno(ID_ALUNO, model);

        assertAll(
                () -> assertEquals("alunos/formulario", viewName),
                () -> assertSame(aluno, model.getAttribute("aluno")),
                () -> assertEquals(List.of(curso), model.getAttribute("listaCursos"))
        );
    }

    @Test
    void salvarAlunoDeveAssociarCursoERedirecionarParaLista() {
        Aluno aluno = criarAlunoPadrao();
        Curso curso = criarCurso();
        when(cursoService.buscarObrigatorio(ID_CURSO)).thenReturn(curso);

        String viewName = controller.salvarAluno(aluno, ID_CURSO);

        assertAll(
                () -> assertEquals("redirect:/alunos", viewName),
                () -> assertSame(curso, aluno.getCurso())
        );
        verify(alunoService).salvar(aluno);
    }

    @Test
    void excluirAlunoDeveRemoverAlunoERedirecionarParaLista() {
        String viewName = controller.excluirAluno(ID_ALUNO);

        assertEquals("redirect:/alunos", viewName);
        verify(alunoService).excluirPorId(ID_ALUNO);
    }

    @Test
    void novoAlunoDeveRetornarViewDeFormularioEAdicionarCursosAoModel() {
        Model model = new ExtendedModelMap();
        Curso curso = criarCurso();
        when(cursoService.listarTodos()).thenReturn(List.of(curso));

        String viewName = controller.novoAluno(model);

        assertAll(
                () -> assertEquals("alunos/formulario", viewName),
                () -> assertInstanceOf(Aluno.class, model.getAttribute("aluno")),
                () -> assertEquals(List.of(curso), model.getAttribute("listaCursos"))
        );
    }

    @Test
    void formularioDoAlunoDeveConterCamposBasicosECurso() throws IOException {
        String conteudo = lerTemplate("templates/alunos/formulario.html");

        assertAll(
                () -> assertTrue(conteudo.contains("th:action=\"@{/alunos/salvar}\"")),
                () -> assertTrue(conteudo.contains("th:object=\"${aluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{nomeAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{emailAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{telefoneAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{enderecoAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{cpfAluno}\"")),
                () -> assertTrue(conteudo.contains("th:field=\"*{raAluno}\"")),
                () -> assertTrue(conteudo.contains("name=\"cursoId\"")),
                () -> assertTrue(conteudo.contains("th:each=\"curso : ${listaCursos}\""))
        );
    }

    @Test
    void listaDeAlunosDeveConterCursoEAcoesSeguras() throws IOException {
        String conteudo = lerTemplate("templates/alunos/lista.html");

        assertAll(
                () -> assertTrue(conteudo.contains("th:each=\"aluno : ${listaAlunos}\"")),
                () -> assertTrue(conteudo.contains("aluno.curso.nomeCurso")),
                () -> assertTrue(conteudo.contains("th:href=\"@{/alunos/novo}\"")),
                () -> assertTrue(conteudo.contains("th:href=\"@{/alunos/editar/{id}(id=${aluno.idAluno})}\"")),
                () -> assertTrue(conteudo.contains("th:action=\"@{/alunos/excluir/{id}(id=${aluno.idAluno})}\"")),
                () -> assertTrue(conteudo.contains("method=\"post\""))
        );
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

    private Curso criarCurso() {
        Curso curso = new Curso();
        curso.setIdCurso(ID_CURSO);
        curso.setNomeCurso("ADS");
        curso.setPeriodo(6);
        curso.setCargaHoraria(2400);
        return curso;
    }
}
