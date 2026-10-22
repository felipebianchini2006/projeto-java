package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Role;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorLoginUsuario(String login) {
        return usuarioRepository.findByLoginUsuario(login);
    }

    public Optional<Usuario> buscarPorTokenRecuperacaoSenha(String token) {
        return usuarioRepository.findByTokenRecuperacaoSenha(token)
                .filter(this::tokenAindaValido);
    }

    /**
     * Cadastro publico: sempre cria uma nova conta com ROLE_USER,
     * ignorando id/role enviados no formulario.
     */
    @Transactional
    public Usuario cadastrar(Usuario usuario) {
        usuario.setId(null);
        usuario.setRole(Role.ROLE_USER);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void garantirAdmin(String login, String email, String senha) {
        if (usuarioRepository.findByLoginUsuario(login).isPresent()) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setEmail(email);
        admin.setLogin(login);
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setRole(Role.ROLE_ADMIN);
        usuarioRepository.save(admin);
    }

    @Transactional
    public boolean solicitarRecuperacaoSenha(String email, String linkBase) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email.trim());

        if (usuarioOptional.isEmpty()) {
            return true;
        }

        Usuario usuario = usuarioOptional.get();
        String token = UUID.randomUUID().toString();

        if (!emailService.enviarRecuperacaoSenha(usuario, linkBase + "?token=" + token)) {
            return false;
        }

        usuario.setTokenRecuperacaoSenha(token);
        usuario.setExpiracaoTokenRecuperacaoSenha(LocalDateTime.now().plusMinutes(30));
        usuarioRepository.save(usuario);
        return true;
    }

    @Transactional
    public boolean redefinirSenha(String token, String senha) {
        return buscarPorTokenRecuperacaoSenha(token)
                .map(usuario -> {
                    usuario.setSenha(passwordEncoder.encode(senha));
                    usuario.setTokenRecuperacaoSenha(null);
                    usuario.setExpiracaoTokenRecuperacaoSenha(null);
                    usuarioRepository.save(usuario);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public void excluirPorId(Integer id) {
        usuarioRepository.deleteById(id);
    }

    private boolean tokenAindaValido(Usuario usuario) {
        LocalDateTime expiracao = usuario.getExpiracaoTokenRecuperacaoSenha();
        return expiracao != null && expiracao.isAfter(LocalDateTime.now());
    }
}
