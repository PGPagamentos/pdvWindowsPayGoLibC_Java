package br.com.paygo.enums;

/**
 * Tipos de evento a serem ativados para monitoração no PIN-pad
 */
public enum PWPINPadInputEvent {
    KEYS(1),   // Acionamento de teclas
    MAG(2),   // Passagem de cartão magnético
    ICC(4),   // Inserção de cartão com chip.
    CTLS(8);    // Aproximação de um cartão sem contato

    private final int value;

    PWPINPadInputEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
