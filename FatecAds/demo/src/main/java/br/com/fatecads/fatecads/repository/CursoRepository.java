package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
}
