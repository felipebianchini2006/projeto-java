package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    private DisciplinaService disciplinaService;

    @GetMapping({"", "/listar"})
    public String listarDisciplinas(Model model) {
        model.addAttribute("listaDisciplinas", disciplinaService.listarTodos());
        return "disciplinas/lista";
    }

    @GetMapping("/novo")
    public String novaDisciplina(Model model) {
        model.addAttribute("disciplina", new Disciplina());
        return "disciplinas/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarDisciplina(@PathVariable("id") Integer idDisciplina, Model model) {
        Disciplina disciplina = disciplinaService.buscarPorId(idDisciplina).orElseGet(Disciplina::new);
        model.addAttribute("disciplina", disciplina);
        return "disciplinas/formulario";
    }

    @PostMapping("/salvar")
    public String salvarDisciplina(Disciplina disciplina) {
        disciplinaService.salvar(disciplina);
        return "redirect:/disciplinas";
    }

    @PostMapping("/excluir/{id}")
    public String excluirDisciplina(@PathVariable("id") Integer idDisciplina) {
        disciplinaService.excluirPorId(idDisciplina);
        return "redirect:/disciplinas";
    }
}
