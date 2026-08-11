package com.filipecode.icompras.clientes.exception;

import com.filipecode.icompras.clientes.model.ErroResposta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> handleValidacao(MethodArgumentNotValidException e) {
        var erro = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> new ErroResposta("VALIDATION_ERROR", fieldError.getField(), fieldError.getDefaultMessage()))
                .orElseGet(() -> new ErroResposta("VALIDATION_ERROR", null, "Requisição inválida"));
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> handleCorpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ErroResposta("VALIDATION_ERROR", null, "Corpo da requisição inválido"));
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleClienteNaoEncontrado(ClienteNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResposta("CLIENTE_NOT_FOUND", "codigo", e.getMessage()));
    }

    @ExceptionHandler(ClienteJaExistenteException.class)
    public ResponseEntity<ErroResposta> handleClienteJaExistente(ClienteJaExistenteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroResposta("CLIENTE_DUPLICADO", "cpf", e.getMessage()));
    }
}
