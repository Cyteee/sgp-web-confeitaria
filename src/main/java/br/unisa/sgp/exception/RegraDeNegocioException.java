package br.unisa.sgp.exception;

/** Violação de regra de negócio que não é falta de estoque (ex.: insumo em uso, pedido já confirmado). */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
