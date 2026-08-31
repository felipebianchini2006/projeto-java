package br.com.fatecads.fatecads.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlunoNaoEncontradoException extends RuntimeException {

    public AlunoNaoEncontradoException(Integer idAluno) {
        super("Aluno nao encontrado: " + idAluno);
    }
}
