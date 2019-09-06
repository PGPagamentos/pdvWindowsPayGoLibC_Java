package br.com.paygo.exception;

/**
 * Exception lançada quando uma informação solicitada pela biblioteca PGWebLib (PWData) não estiver mapeada na aplicação.
 */
public class InvalidDataType extends InvalidReturnTypeException {

    public InvalidDataType(String message) {
        super(message);
    }
}
