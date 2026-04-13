package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/professores")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping({"", "/listar"})
    public String listarProfessores(Model model) {
        model.addAttribute("listaProfessores", professorService.listarTodos());
        return "professores/lista";
    }

    @GetMapping("/novo")
    public String novoProfessor(Model model) {
        model.addAttribute("professor", new Professor());
        return "professores/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarProfessor(@PathVariable("id") Integer idProfessor, Model model) {
        Professor professor = professorService.buscarPorId(idProfessor).orElseGet(Professor::new);
        model.addAttribute("professor", professor);
        return "professores/formulario";
    }

    @PostMapping("/salvar")
    public String salvarProfessor(Professor professor) {
        professorService.salvar(professor);
        return "redirect:/professores";
    }

    @PostMapping("/excluir/{id}")
    public String excluirProfessor(@PathVariable("id") Integer idProfessor) {
        professorService.excluirPorId(idProfessor);
        return "redirect:/professores";
    }
}
