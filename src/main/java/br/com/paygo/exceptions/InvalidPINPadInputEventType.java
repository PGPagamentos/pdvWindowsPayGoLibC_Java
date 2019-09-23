package br.com.paygo.exceptions;

/**
 * Exception lançada quando um código de input no PIN-pad (PWPINPadInputEvent) não está mapeado na aplicação.
 */
public class InvalidPINPadInputEventType extends Exception {
    public InvalidPINPadInputEventType(String message) {
        super(message);
    }
}
