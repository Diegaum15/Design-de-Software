package com.seucantinho.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO de resposta de erro padronizada
    private record ErrorDetails(LocalDateTime timestamp, String message, String details) {}

    /**
     * Trata EntityNotFoundException (404 NOT FOUND).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Recurso não encontrado",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND); // 404
    }

    /**
     * Trata ValidacaoException e ReservaIndisponivelException (400 BAD REQUEST).
     */
    @ExceptionHandler({ValidacaoException.class, ReservaIndisponivelException.class})
    public ResponseEntity<ErrorDetails> handleBadRequestExceptions(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Requisição inválida",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST); // 400
    }

    /**
     * Trata erros de validação de DTOs (@Valid) (400 BAD REQUEST).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Erros de validação nos campos",
                errors
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST); // 400
    }

    /**
     * Trata todas as outras exceções não capturadas (500 INTERNAL SERVER ERROR).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Erro interno do servidor",
                ex.getMessage()
        );
        // Logar exceção completa aqui para debug
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}