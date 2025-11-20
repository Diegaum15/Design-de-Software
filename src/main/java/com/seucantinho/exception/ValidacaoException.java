package com.seucantinho.exception;

// Exceções de regras de negócio devem estender RuntimeException
public class ValidacaoException extends RuntimeException {

    // Construtor que aceita a mensagem de erro
    public ValidacaoException(String message) {
        super(message);
    }
}