package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/recuperar-senha")
    public String recuperarSenha() {
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String enviarRecuperacaoSenha(@RequestParam String email,
                                         HttpServletRequest request,
                                         Model model) {
        boolean emailEnviado = usuarioService.solicitarRecuperacaoSenha(email, criarLinkBaseRecuperacao(request));

        if (emailEnviado) {
            model.addAttribute("mensagem", "Se o e-mail estiver cadastrado, voce recebera um link para redefinir a senha.");
        } else {
            model.addAttribute("erro", "Envio de e-mail ainda nao configurado. Configure o Resend nas variaveis de ambiente.");
        }

        return "recuperar-senha";
    }

    @GetMapping("/redefinir-senha")
    public String redefinirSenha(@RequestParam String token, Model model) {
        if (usuarioService.buscarPorTokenRecuperacaoSenha(token).isEmpty()) {
            model.addAttribute("erro", "Link de recuperacao invalido ou expirado.");
            return "recuperar-senha";
        }

        model.addAttribute("token", token);
        return "redefinir-senha";
    }

    @PostMapping("/redefinir-senha")
    public String salvarNovaSenha(@RequestParam String token,
                                  @RequestParam String senha,
                                  @RequestParam String confirmarSenha,
                                  Model model) {
        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("token", token);
            model.addAttribute("erro", "As senhas nao conferem.");
            return "redefinir-senha";
        }

        if (!usuarioService.redefinirSenha(token, senha)) {
            model.addAttribute("erro", "Link de recuperacao invalido ou expirado.");
            return "recuperar-senha";
        }

        return "redirect:/login?senhaRedefinida";
    }

    private String criarLinkBaseRecuperacao(HttpServletRequest request) {
        String urlAtual = request.getRequestURL().toString();
        return urlAtual.substring(0, urlAtual.length() - request.getServletPath().length()) + "/redefinir-senha";
    }
}
