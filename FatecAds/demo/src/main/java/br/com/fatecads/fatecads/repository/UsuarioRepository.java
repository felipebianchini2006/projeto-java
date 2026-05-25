package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("select usuario from Usuario usuario where usuario.login = :login")
    Optional<Usuario> findByLoginUsuario(@Param("login") String login);

    @Query("select usuario from Usuario usuario where lower(usuario.email) = lower(:email)")
    Optional<Usuario> findByEmail(@Param("email") String email);

    Optional<Usuario> findByTokenRecuperacaoSenha(String tokenRecuperacaoSenha);

}
