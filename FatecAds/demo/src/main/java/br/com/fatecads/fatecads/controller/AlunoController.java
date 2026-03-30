package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping({"", "/listar"})
    public String listarAlunos(Model model) {
        model.addAttribute("listaAlunos", alunoService.listarTodos());
        return "alunos/lista";
    }

    @GetMapping("/novo")
    public String novoAluno(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "alunos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editarAluno(@PathVariable("id") Integer idAluno, Model model) {
        Aluno aluno = alunoService.buscarPorId(idAluno).orElseGet(Aluno::new);
        model.addAttribute("aluno", aluno);
        return "alunos/formulario";
    }

    @PostMapping("/salvar")
    public String salvarAluno(Aluno aluno) {
        alunoService.salvar(aluno);
        return "redirect:/alunos";
    }

    @PostMapping("/excluir/{id}")
    public String excluirAluno(@PathVariable("id") Integer idAluno) {
        alunoService.excluirPorId(idAluno);
        return "redirect:/alunos";
    }
}
