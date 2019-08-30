package br.com.paygo.enums;

/**
 * Tipos de cartão aceitos
 */
public enum PWCardType {
    NOT_DEFINED(0),
    CREDIT(1),
    DEBIT(2),
    VOUCHER(4),
    OTHER(8);

    private final int value;

    PWCardType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
