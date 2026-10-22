package br.com.fatecads.fatecads.security;

import br.com.fatecads.fatecads.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Garante que exista um usuario ROLE_ADMIN ao subir a aplicacao,
 * ja que o cadastro publico sempre cria contas ROLE_USER.
 */
@Component
public class AdminInitializer implements ApplicationRunner {

    private final UsuarioService usuarioService;

    @Value("${app.admin.login:admin}")
    private String login;

    @Value("${app.admin.email:admin@fatecads.local}")
    private String email;

    @Value("${app.admin.senha:admin123}")
    private String senha;

    public AdminInitializer(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(ApplicationArguments args) {
        usuarioService.garantirAdmin(login, email, senha);
    }
}
