package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.service.CursoService;
import br.com.fatecads.fatecads.service.DisciplinaService;
import br.com.fatecads.fatecads.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private CursoService cursoService;

    @GetMapping({"", "/listar"})
    public String listarDisciplinas(Model model) {
        model.addAttribute("listaDisciplinas", disciplinaService.listarTodos());
        return "disciplinas/lista";
    }

    @GetMapping("/novo")
    public String novaDisciplina(Model model) {
        model.addAttribute("disciplina", new Disciplina());
        carregarRelacionamentos(model);
        return "disciplinas/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarDisciplina(@PathVariable("id") Integer idDisciplina, Model model) {
        Disciplina disciplina = disciplinaService.buscarPorId(idDisciplina).orElseGet(Disciplina::new);
        model.addAttribute("disciplina", disciplina);
        carregarRelacionamentos(model);
        return "disciplinas/formulario";
    }

    @PostMapping("/salvar")
    public String salvarDisciplina(Disciplina disciplina,
                                   @RequestParam("professorId") Integer professorId,
                                   @RequestParam("cursoId") Integer cursoId) {
        disciplina.setProfessor(professorService.buscarPorId(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor nao encontrado")));
        disciplina.setCurso(cursoService.buscarPorId(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso nao encontrado")));
        disciplinaService.salvar(disciplina);
        return "redirect:/disciplinas";
    }

    @PostMapping("/excluir/{id}")
    public String excluirDisciplina(@PathVariable("id") Integer idDisciplina) {
        disciplinaService.excluirPorId(idDisciplina);
        return "redirect:/disciplinas";
    }

    private void carregarRelacionamentos(Model model) {
        model.addAttribute("listaProfessores", professorService.listarTodos());
        model.addAttribute("listaCursos", cursoService.listarTodos());
    }
}
