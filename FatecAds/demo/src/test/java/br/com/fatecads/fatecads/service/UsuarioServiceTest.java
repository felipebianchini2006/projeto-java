package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.demo.DemoApplication;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {DemoApplication.class, UsuarioServiceTest.MailTestConfig.class})
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CapturingEmailService emailService;

    @BeforeEach
    void limparDados() {
        usuarioRepository.deleteAll();
        emailService.limpar();
    }

    @Test
    void solicitarRecuperacaoSenhaDeveGerarTokenEEnviarEmail() {
        usuarioService.salvar(criarUsuario());

        boolean enviado = usuarioService.solicitarRecuperacaoSenha(
                "MARIA@EMAIL.COM",
                "http://localhost:8080/redefinir-senha"
        );

        Usuario usuario = usuarioRepository.findByEmail("maria@email.com").orElseThrow();
        EmailEnviado email = emailService.getEmails().get(0);

        assertAll(
                () -> assertTrue(enviado),
                () -> assertNotNull(usuario.getTokenRecuperacaoSenha()),
                () -> assertNotNull(usuario.getExpiracaoTokenRecuperacaoSenha()),
                () -> assertTrue(usuario.getExpiracaoTokenRecuperacaoSenha().isAfter(LocalDateTime.now())),
                () -> assertTrue(email.linkRecuperacao().contains(usuario.getTokenRecuperacaoSenha())),
                () -> assertTrue(email.linkRecuperacao().contains("http://localhost:8080/redefinir-senha?token="))
        );
    }

    @Test
    void redefinirSenhaDeveAtualizarSenhaELimparToken() {
        usuarioService.salvar(criarUsuario());
        usuarioService.solicitarRecuperacaoSenha("maria@email.com", "http://localhost:8080/redefinir-senha");
        String token = usuarioRepository.findByEmail("maria@email.com").orElseThrow().getTokenRecuperacaoSenha();

        boolean redefinida = usuarioService.redefinirSenha(token, "nova123");

        Usuario usuario = usuarioRepository.findByEmail("maria@email.com").orElseThrow();
        assertAll(
                () -> assertTrue(redefinida),
                () -> assertTrue(passwordEncoder.matches("nova123", usuario.getSenha())),
                () -> assertNull(usuario.getTokenRecuperacaoSenha()),
                () -> assertNull(usuario.getExpiracaoTokenRecuperacaoSenha())
        );
    }

    @Test
    void redefinirSenhaNaoDeveAceitarTokenExpirado() {
        Usuario usuario = criarUsuario();
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setTokenRecuperacaoSenha("token-expirado");
        usuario.setExpiracaoTokenRecuperacaoSenha(LocalDateTime.now().minusMinutes(1));
        usuarioRepository.save(usuario);

        boolean redefinida = usuarioService.redefinirSenha("token-expirado", "nova123");

        Usuario usuarioSalvo = usuarioRepository.findByEmail("maria@email.com").orElseThrow();
        assertAll(
                () -> assertFalse(redefinida),
                () -> assertTrue(passwordEncoder.matches("senha123", usuarioSalvo.getSenha())),
                () -> assertNotNull(usuarioSalvo.getTokenRecuperacaoSenha())
        );
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        usuario.setLogin("maria");
        usuario.setSenha("senha123");
        return usuario;
    }

    @TestConfiguration
    static class MailTestConfig {

        @Bean
        @Primary
        CapturingEmailService emailService() {
            return new CapturingEmailService();
        }
    }

    static class CapturingEmailService implements EmailService {

        private final List<EmailEnviado> emails = new ArrayList<>();

        List<EmailEnviado> getEmails() {
            return emails;
        }

        void limpar() {
            emails.clear();
        }

        @Override
        public boolean enviarRecuperacaoSenha(Usuario usuario, String linkRecuperacao) {
            emails.add(new EmailEnviado(usuario.getEmail(), linkRecuperacao));
            return true;
        }
    }

    record EmailEnviado(String destinatario, String linkRecuperacao) {
    }
}
