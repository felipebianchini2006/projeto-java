package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Disciplina;
import br.com.fatecads.fatecads.repository.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public List<Disciplina> listarTodos() {
        return disciplinaRepository.findAll();
    }

    public Optional<Disciplina> buscarPorId(Integer idDisciplina) {
        return disciplinaRepository.findById(idDisciplina);
    }

    @Transactional
    public Disciplina salvar(Disciplina disciplina) {
        return disciplinaRepository.save(disciplina);
    }

    @Transactional
    public void excluirPorId(Integer idDisciplina) {
        disciplinaRepository.deleteById(idDisciplina);
    }
}
