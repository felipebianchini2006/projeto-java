package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.service.PedidoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** Historico de compras: admin ve todos os pedidos; usuario ve apenas os seus. */
    @GetMapping
    public String historico(Model model, Principal principal, HttpServletRequest request) {
        boolean admin = request.isUserInRole("ADMIN");
        model.addAttribute("todos", admin);
        model.addAttribute("pedidos", pedidoService.listarHistorico(principal.getName(), admin));
        return "pedidos/historico";
    }
}
