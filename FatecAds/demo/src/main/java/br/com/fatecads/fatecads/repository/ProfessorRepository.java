package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
}
