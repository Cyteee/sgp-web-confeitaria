package br.unisa.sgp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(PedidoNaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({EstoqueInsuficienteException.class, RegraDeNegocioException.class})
    public ResponseEntity<Map<String, String>> conflito(RuntimeException e) {
        return resposta(HttpStatus.CONFLICT, e);
    }

    private ResponseEntity<Map<String, String>> resposta(HttpStatus status, RuntimeException e) {
        return ResponseEntity.status(status).body(Map.of("erro", e.getMessage()));
    }
}
