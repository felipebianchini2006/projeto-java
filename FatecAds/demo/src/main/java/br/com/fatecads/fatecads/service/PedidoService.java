package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.dto.CriarPedidoRequest;
import br.com.fatecads.fatecads.dto.ItemPedidoRequest;
import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.ItemPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.entity.Role;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.exception.PedidoInvalidoException;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final AlunoService alunoService;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            AlunoService alunoService,
            ProdutoService produtoService,
            UsuarioService usuarioService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.alunoService = alunoService;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Cria o pedido em nome do usuario logado (chamado ao finalizar o carrinho).
     * Vincular um aluno e opcional e permitido apenas para ROLE_ADMIN.
     */
    @Transactional
    public Pedido criar(CriarPedidoRequest request, String loginUsuario) {
        validarRequest(request);

        Usuario usuario = usuarioService.buscarPorLoginUsuario(loginUsuario)
                .orElseThrow(() -> new PedidoInvalidoException("Usuario nao encontrado"));
        Aluno aluno = buscarAlunoOpcional(request.alunoId(), usuario);
        Pedido pedido = new Pedido(usuario, aluno, LocalDate.now());
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequest itemRequest : request.itens()) {
            validarItem(itemRequest);
            Produto produto = produtoService.buscarObrigatorio(itemRequest.produtoId());
            ItemPedido item = new ItemPedido(
                    pedido,
                    produto,
                    itemRequest.quantidade(),
                    produto.getValor()
            );
            pedido.adicionarItem(item);
            total = total.add(item.getSubtotal());
        }

        pedido.definirTotal(total.setScale(2, RoundingMode.HALF_UP));
        return pedidoRepository.save(pedido);
    }

    /** Historico de compras: o admin ve todos os pedidos, o usuario so os proprios. */
    @Transactional(readOnly = true)
    public List<Pedido> listarHistorico(String loginUsuario, boolean todos) {
        return todos
                ? pedidoRepository.findAllByOrderByIdPedidoDesc()
                : pedidoRepository.findByUsuarioLoginOrderByIdPedidoDesc(loginUsuario);
    }

    private Aluno buscarAlunoOpcional(Integer alunoId, Usuario usuario) {
        if (alunoId == null) {
            return null;
        }
        if (usuario.getRole() != Role.ROLE_ADMIN) {
            throw new PedidoInvalidoException("Apenas administradores podem vincular um aluno ao pedido");
        }
        return alunoService.buscarObrigatorio(alunoId);
    }

    private void validarRequest(CriarPedidoRequest request) {
        if (request == null) {
            throw new PedidoInvalidoException("Dados do pedido invalidos");
        }
        if (request.itens() == null || request.itens().isEmpty()) {
            throw new PedidoInvalidoException("Adicione pelo menos um item ao pedido");
        }
    }

    private void validarItem(ItemPedidoRequest item) {
        if (item == null || item.produtoId() == null) {
            throw new PedidoInvalidoException("Informe o produto");
        }
        if (item.quantidade() == null || item.quantidade() <= 0) {
            throw new PedidoInvalidoException("A quantidade deve ser maior que zero");
        }
    }
}
