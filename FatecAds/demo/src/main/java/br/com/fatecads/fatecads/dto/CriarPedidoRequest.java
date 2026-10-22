package br.com.fatecads.fatecads.dto;

import java.util.List;

/**
 * Comando interno para criar um pedido a partir do carrinho.
 * {@code alunoId} e opcional; somente ROLE_ADMIN pode vincular um aluno (validado no PedidoService).
 */
public record CriarPedidoRequest(
        Integer alunoId,
        List<ItemPedidoRequest> itens
) {
}
