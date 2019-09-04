package br.com.paygo.exception;

/**
 * Exception lançada quando um tipo de dado retornado pela biblioteca PGWebLib não estiver mapeado na aplicação.
 */
public class InvalidReturnTypeException extends Exception {

    public InvalidReturnTypeException(String message) {
        super(message);
    }
}
