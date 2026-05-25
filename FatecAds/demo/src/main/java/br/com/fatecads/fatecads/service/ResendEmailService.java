package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class ResendEmailService implements EmailService {

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${resend.from:Fatec ADS <noreply@felipeb.tech>}")
    private String remetente;

    @Value("${resend.api-url:https://api.resend.com}")
    private String apiUrl;

    @Override
    public boolean enviarRecuperacaoSenha(Usuario usuario, String linkRecuperacao) {
        if (!StringUtils.hasText(apiKey)) {
            return false;
        }

        try {
            RestClient.create(apiUrl)
                    .post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(criarCorpoEmail(usuario, linkRecuperacao))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException ex) {
            return false;
        }
    }

    private Map<String, Object> criarCorpoEmail(Usuario usuario, String linkRecuperacao) {
        String texto = "Ola, " + usuario.getNome() + ".\n\n"
                + "Acesse o link abaixo para redefinir sua senha. Ele expira em 30 minutos:\n"
                + linkRecuperacao + "\n\n"
                + "Se voce nao solicitou a recuperacao, ignore este e-mail.";

        String html = "<p>Ola, " + escaparHtml(usuario.getNome()) + ".</p>"
                + "<p>Acesse o link abaixo para redefinir sua senha. Ele expira em 30 minutos:</p>"
                + "<p><a href=\"" + escaparHtml(linkRecuperacao) + "\">Redefinir senha</a></p>"
                + "<p>Se voce nao solicitou a recuperacao, ignore este e-mail.</p>";

        return Map.of(
                "from", remetente,
                "to", List.of(usuario.getEmail()),
                "subject", "Recuperacao de senha - Fatec ADS",
                "text", texto,
                "html", html
        );
    }

    private String escaparHtml(String valor) {
        return valor
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
