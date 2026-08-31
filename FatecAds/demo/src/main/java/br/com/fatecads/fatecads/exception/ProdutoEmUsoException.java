package br.com.fatecads.fatecads.exception;

public class ProdutoEmUsoException extends RuntimeException {

    public ProdutoEmUsoException(Integer idProduto) {
        super("O produto " + idProduto + " esta associado a um pedido e nao pode ser excluido");
    }
}
