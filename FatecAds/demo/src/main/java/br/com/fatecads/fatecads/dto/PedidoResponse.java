package br.com.fatecads.fatecads.dto;

import br.com.fatecads.fatecads.entity.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoResponse(
        Integer idPedido,
        LocalDate dataPedido,
        BigDecimal total
) {

    public static PedidoResponse from(Pedido pedido) {
        return new PedidoResponse(
                pedido.getIdPedido(),
                pedido.getDataPedido(),
                pedido.getTotal()
        );
    }
}
