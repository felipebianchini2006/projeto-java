package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.dto.ApiError;
import br.com.fatecads.fatecads.exception.AlunoNaoEncontradoException;
import br.com.fatecads.fatecads.exception.PedidoInvalidoException;
import br.com.fatecads.fatecads.exception.ProdutoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PedidoController.class)
public class PedidoApiExceptionHandler {

    @ExceptionHandler({AlunoNaoEncontradoException.class, ProdutoNaoEncontradoException.class})
    public ResponseEntity<ApiError> handleNaoEncontrado(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(exception.getMessage()));
    }

    @ExceptionHandler(PedidoInvalidoException.class)
    public ResponseEntity<ApiError> handlePedidoInvalido(PedidoInvalidoException exception) {
        return ResponseEntity.badRequest().body(new ApiError(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidacao(MethodArgumentNotValidException exception) {
        String mensagem = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Pedido invalido");
        return ResponseEntity.badRequest().body(new ApiError(mensagem));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonInvalido() {
        return ResponseEntity.badRequest().body(new ApiError("Dados do pedido invalidos"));
    }
}
