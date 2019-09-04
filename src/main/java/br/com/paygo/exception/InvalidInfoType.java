package br.com.paygo.exception;

/**
 * Exception lançada quando um tipo de dado que deve ser retornado para o biblioteca PGWebLib não estiver mapeado na aplicação.
 */
public class InvalidInfoType extends InvalidReturnTypeException {

    public InvalidInfoType(String message) {
        super(message);
    }
}
