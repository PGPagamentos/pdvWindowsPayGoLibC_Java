package br.com.paygo.exceptions;

import br.com.paygo.exception.InvalidReturnTypeException;

/**
 * Exception lançada quando um código de input no PIN-pad (PWPINPadInputEvent) não está mapeado na aplicação.
 */
public class InvalidPINPadInputEventType extends InvalidReturnTypeException {
    public InvalidPINPadInputEventType(String message) {
        super(message);
    }
}
