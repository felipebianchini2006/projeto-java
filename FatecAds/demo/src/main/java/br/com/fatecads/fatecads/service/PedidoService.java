package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.dto.CriarPedidoRequest;
import br.com.fatecads.fatecads.dto.ItemPedidoRequest;
import br.com.fatecads.fatecads.dto.PedidoResponse;
import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.ItemPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.exception.PedidoInvalidoException;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final AlunoService alunoService;
    private final ProdutoService produtoService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            AlunoService alunoService,
            ProdutoService produtoService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.alunoService = alunoService;
        this.produtoService = produtoService;
    }

    @Transactional
    public PedidoResponse criar(CriarPedidoRequest request) {
        validarRequest(request);

        Aluno aluno = alunoService.buscarObrigatorio(request.alunoId());
        Pedido pedido = new Pedido(aluno, LocalDate.now());
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
        return PedidoResponse.from(pedidoRepository.save(pedido));
    }

    private void validarRequest(CriarPedidoRequest request) {
        if (request == null || request.alunoId() == null) {
            throw new PedidoInvalidoException("Informe o aluno");
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
