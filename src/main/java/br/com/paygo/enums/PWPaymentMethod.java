package br.com.paygo.enums;

/**
 * Formas de Pagamento aceitos
 */
public enum PWPaymentMethod {
    NOT_DEFINED(0),
    A_VISTA(1),
    PARCELADO_EMISSOR(2),
    PARCELADO_ESTABELECIMENTO(4),
    PRE_DATADO(8);

    private final int value;

    PWPaymentMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
