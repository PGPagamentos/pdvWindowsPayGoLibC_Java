package br.com.paygo.exception;

/**
 * Exception lançada quando um código de confirmação (PWCnf) informado não está mapeado na aplicação.
 */
public class InvalidConfirmationType extends InvalidReturnTypeException {
    public InvalidConfirmationType(String message) {
        super(message);
    }
}
