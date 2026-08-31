package br.com.fatecads.fatecads.exception;

public class ImagemProdutoInvalidaException extends RuntimeException {

    public ImagemProdutoInvalidaException(String mensagem) {
        super(mensagem);
    }

    public ImagemProdutoInvalidaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
