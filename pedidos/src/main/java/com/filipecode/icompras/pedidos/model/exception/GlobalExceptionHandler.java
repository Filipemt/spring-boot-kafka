package com.filipecode.icompras.pedidos.model.exception;

import com.filipecode.icompras.pedidos.model.ErroResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErroResposta> handleValidationException(ValidationException e) {
        var erro = new ErroResposta(
                "VALIDATION_ERROR",
                e.getField(), 
                e.getMessage()
        );

        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(ItemNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleItemNaoEncontradoException(ItemNaoEncontradoException e) {
        var erro = new ErroResposta(
                "ITEM_NOT_FOUND",
                "codigoPedido",
                e.getMessage()
        );

        return ResponseEntity.badRequest().body(erro);
    }
}
