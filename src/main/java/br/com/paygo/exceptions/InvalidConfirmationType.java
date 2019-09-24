package br.com.paygo.exceptions;

/**
 * Exception lançada quando um código de confirmação (PWCnf) informado não está mapeado na aplicação.
 */
public class InvalidConfirmationType extends Exception {
    public InvalidConfirmationType(String message) {
        super(message);
    }
}
