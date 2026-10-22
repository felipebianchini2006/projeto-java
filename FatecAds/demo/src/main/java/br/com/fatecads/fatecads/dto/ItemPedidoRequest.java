package br.com.fatecads.fatecads.dto;

public record ItemPedidoRequest(
        Integer produtoId,
        Integer quantidade
) {
}
