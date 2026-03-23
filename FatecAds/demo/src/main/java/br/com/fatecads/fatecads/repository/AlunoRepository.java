package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
}
