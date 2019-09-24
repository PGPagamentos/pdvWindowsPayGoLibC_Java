package br.com.paygo.exceptions;

/**
 * Exception lançada quando uma informação solicitada pela biblioteca PGWebLib (PWData) não estiver mapeada na aplicação.
 */
public class InvalidDataType extends Exception {

    public InvalidDataType(String message) {
        super(message);
    }
}
