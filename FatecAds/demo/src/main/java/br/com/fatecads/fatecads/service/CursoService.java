package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.exception.CursoNaoEncontradoException;
import br.com.fatecads.fatecads.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> buscarPorId(Integer idCurso) {
        return cursoRepository.findById(idCurso);
    }

    public Curso buscarObrigatorio(Integer idCurso) {
        return buscarPorId(idCurso)
                .orElseThrow(() -> new CursoNaoEncontradoException(idCurso));
    }

    @Transactional
    public Curso salvar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Transactional
    public void excluirPorId(Integer idCurso) {
        cursoRepository.deleteById(idCurso);
    }
}
