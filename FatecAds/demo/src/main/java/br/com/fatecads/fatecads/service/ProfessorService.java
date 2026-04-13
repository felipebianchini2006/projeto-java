package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }

    public Optional<Professor> buscarPorId(Integer idProfessor) {
        return professorRepository.findById(idProfessor);
    }

    @Transactional
    public Professor salvar(Professor professor) {
        return professorRepository.save(professor);
    }

    @Transactional
    public void excluirPorId(Integer idProfessor) {
        professorRepository.deleteById(idProfessor);
    }
}
