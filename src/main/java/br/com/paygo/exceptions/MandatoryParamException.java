package br.com.paygo.exceptions;

/**
 * Exception lançada quando o ocorre algum erro ao adicionar os parâmetros obrigatórios da aplicação.
 */
public class MandatoryParamException extends Exception {

    public MandatoryParamException(String message) {
        super(message);
    }
}
