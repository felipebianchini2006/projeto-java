package br.com.fatecads.fatecads.security;

import br.com.fatecads.fatecads.demo.DemoApplication;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.entity.Role;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.ItemCarrinhoRepository;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@Transactional
class AutorizacaoPorRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Test
    @WithMockUser(roles = "USER")
    void usuarioVeProdutosComBotaoDeCarrinhoMasSemAcoesDeAdmin() throws Exception {
        produtoRepository.save(criarProduto("Lapis", "1.00"));

        mockMvc.perform(get("/produtos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("/carrinho/adicionar")))
                .andExpect(content().string(not(containsString("Novo produto"))))
                .andExpect(content().string(not(containsString("/produtos/excluir/"))));
    }

    @Test
    @WithMockUser(username = "carrinho", roles = "USER")
    void usuarioAdicionaPelaPaginaDeProdutosFinalizaEVeNoHistorico() throws Exception {
        usuarioRepository.save(criarUsuario("carrinho"));
        Integer idProduto = produtoRepository.save(criarProduto("Caneta", "2.50")).getIdProduto();

        mockMvc.perform(post("/carrinho/adicionar").with(csrf())
                        .param("produtoId", idProduto.toString())
                        .param("quantidade", "2"))
                .andExpect(redirectedUrl("/produtos"));
        mockMvc.perform(get("/carrinho")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Caneta")))
                .andExpect(content().string(not(containsString("id=\"alunoId\""))));

        mockMvc.perform(post("/carrinho/finalizar").with(csrf()))
                .andExpect(redirectedUrl("/pedidos"));

        List<Pedido> pedidos = pedidoRepository.findByUsuarioLoginOrderByIdPedidoDesc("carrinho");
        assertAll(
                () -> assertEquals(1, pedidos.size()),
                () -> assertNull(pedidos.get(0).getAluno()),
                () -> assertEquals(new BigDecimal("5.00"), pedidos.get(0).getTotal()),
                () -> assertTrue(itemCarrinhoRepository.findByUsuarioLoginOrderByIdItemCarrinho("carrinho").isEmpty())
        );
        mockMvc.perform(get("/pedidos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("2x Caneta")));
        // Outro usuario nao enxerga o historico alheio
        mockMvc.perform(get("/pedidos").with(user("outro").roles("USER"))).andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Caneta"))));
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioNaoPodeAcessarFuncoesDeAdmin() throws Exception {
        mockMvc.perform(get("/alunos/listar")).andExpect(status().isForbidden());
        mockMvc.perform(get("/cursos/listar")).andExpect(status().isForbidden());
        mockMvc.perform(get("/produtos/novo")).andExpect(status().isForbidden());
        mockMvc.perform(get("/produtos/editar/1")).andExpect(status().isForbidden());
        mockMvc.perform(post("/produtos/excluir/1").with(csrf())).andExpect(status().isForbidden());
        mockMvc.perform(get("/h2-console")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPodeAcessarTudo() throws Exception {
        mockMvc.perform(get("/alunos/listar")).andExpect(status().isOk());
        mockMvc.perform(get("/produtos/novo")).andExpect(status().isOk());
        mockMvc.perform(get("/produtos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Novo produto")));
        mockMvc.perform(get("/carrinho")).andExpect(status().isOk());
        mockMvc.perform(get("/pedidos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Historico de pedidos")));
    }

    @Test
    void cadastroPublicoSempreCriaUsuarioComumMesmoSeEnviarRoleAdmin() throws Exception {
        mockMvc.perform(post("/usuarios/salvar").with(csrf())
                        .param("nome", "Invasor")
                        .param("email", "invasor@email.com")
                        .param("login", "invasor")
                        .param("senha", "senha123")
                        .param("role", "ROLE_ADMIN"))
                .andExpect(status().is3xxRedirection());

        Usuario usuario = usuarioRepository.findByLoginUsuario("invasor").orElseThrow();
        assertEquals(Role.ROLE_USER, usuario.getRole());
    }

    @Test
    void adminPadraoECriadoNaInicializacao() {
        Usuario admin = usuarioRepository.findByLoginUsuario("admin").orElseThrow();
        assertEquals(Role.ROLE_ADMIN, admin.getRole());
    }

    private Usuario criarUsuario(String login) {
        Usuario usuario = new Usuario();
        usuario.setNome(login);
        usuario.setEmail(login + "@email.com");
        usuario.setLogin(login);
        usuario.setSenha("hash");
        return usuario;
    }

    private Produto criarProduto(String descricao, String valor) {
        Produto produto = new Produto();
        produto.setDescricao(descricao);
        produto.setMarca("Marca");
        produto.setUnidade("UN");
        produto.setValor(new BigDecimal(valor));
        return produto;
    }
}
