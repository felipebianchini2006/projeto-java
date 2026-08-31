package br.com.fatecads.fatecads.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CriarPedidoRequest(
        @NotNull(message = "Informe o aluno")
        Integer alunoId,

        @NotEmpty(message = "Adicione pelo menos um item ao pedido")
        List<@Valid ItemPedidoRequest> itens
) {
}
