package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.dto.CriarPedidoRequest;
import br.com.fatecads.fatecads.dto.ItemPedidoRequest;
import br.com.fatecads.fatecads.entity.ItemCarrinho;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.exception.PedidoInvalidoException;
import br.com.fatecads.fatecads.repository.ItemCarrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CarrinhoService {

    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;

    public CarrinhoService(
            ItemCarrinhoRepository itemCarrinhoRepository,
            ProdutoService produtoService,
            UsuarioService usuarioService,
            PedidoService pedidoService
    ) {
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
    }

    public List<ItemCarrinho> listarItens(String login) {
        return itemCarrinhoRepository.findByUsuarioLoginOrderByIdItemCarrinho(login);
    }

    public BigDecimal calcularTotal(List<ItemCarrinho> itens) {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }

    /** Quantidade total de unidades no carrinho (badge do cabecalho). */
    public long contarItens(String login) {
        return itemCarrinhoRepository.somarQuantidadePorLogin(login);
    }

    /** Adiciona o produto; se ja estiver no carrinho, soma a quantidade. */
    @Transactional
    public ItemCarrinho adicionar(String login, Integer produtoId, Integer quantidade) {
        validarQuantidade(quantidade);
        return itemCarrinhoRepository.findByUsuarioLoginAndProdutoIdProduto(login, produtoId)
                .map(item -> {
                    item.somarQuantidade(quantidade);
                    return item;
                })
                .orElseGet(() -> itemCarrinhoRepository.save(new ItemCarrinho(
                        buscarUsuario(login),
                        produtoService.buscarObrigatorio(produtoId),
                        quantidade
                )));
    }

    @Transactional
    public void atualizarQuantidade(String login, Integer produtoId, Integer quantidade) {
        validarQuantidade(quantidade);
        itemCarrinhoRepository.findByUsuarioLoginAndProdutoIdProduto(login, produtoId)
                .orElseThrow(() -> new PedidoInvalidoException("Produto nao esta no carrinho"))
                .definirQuantidade(quantidade);
    }

    @Transactional
    public void remover(String login, Integer produtoId) {
        itemCarrinhoRepository.findByUsuarioLoginAndProdutoIdProduto(login, produtoId)
                .ifPresent(itemCarrinhoRepository::delete);
    }

    /**
     * Transforma o carrinho em um pedido (precos congelados no momento da compra)
     * e esvazia o carrinho. {@code alunoId} e opcional e so aceito para ROLE_ADMIN.
     */
    @Transactional
    public Pedido finalizar(String login, Integer alunoId) {
        List<ItemCarrinho> itens = listarItens(login);
        if (itens.isEmpty()) {
            throw new PedidoInvalidoException("Seu carrinho esta vazio");
        }

        CriarPedidoRequest request = new CriarPedidoRequest(
                alunoId,
                itens.stream()
                        .map(item -> new ItemPedidoRequest(item.getProduto().getIdProduto(), item.getQuantidade()))
                        .toList()
        );
        Pedido pedido = pedidoService.criar(request, login);
        itemCarrinhoRepository.deleteAll(itens);
        return pedido;
    }

    private Usuario buscarUsuario(String login) {
        return usuarioService.buscarPorLoginUsuario(login)
                .orElseThrow(() -> new PedidoInvalidoException("Usuario nao encontrado"));
    }

    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new PedidoInvalidoException("A quantidade deve ser maior que zero");
        }
    }
}
