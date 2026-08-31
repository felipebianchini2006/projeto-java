package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final CursoService cursoService;

    public AlunoController(AlunoService alunoService, CursoService cursoService) {
        this.alunoService = alunoService;
        this.cursoService = cursoService;
    }

    @GetMapping({"", "/listar"})
    public String listarAlunos(Model model) {
        model.addAttribute("listaAlunos", alunoService.listarTodos());
        return "alunos/lista";
    }

    @GetMapping("/novo")
    public String novoAluno(Model model) {
        model.addAttribute("aluno", new Aluno());
        carregarCursos(model);
        return "alunos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarAluno(@PathVariable("id") Integer idAluno, Model model) {
        Aluno aluno = alunoService.buscarObrigatorio(idAluno);
        model.addAttribute("aluno", aluno);
        carregarCursos(model);
        return "alunos/formulario";
    }

    @PostMapping("/salvar")
    public String salvarAluno(Aluno aluno, @RequestParam("cursoId") Integer cursoId) {
        aluno.setCurso(cursoService.buscarObrigatorio(cursoId));
        alunoService.salvar(aluno);
        return "redirect:/alunos";
    }

    @PostMapping("/excluir/{id}")
    public String excluirAluno(@PathVariable("id") Integer idAluno) {
        alunoService.excluirPorId(idAluno);
        return "redirect:/alunos";
    }

    private void carregarCursos(Model model) {
        model.addAttribute("listaCursos", cursoService.listarTodos());
    }
}
