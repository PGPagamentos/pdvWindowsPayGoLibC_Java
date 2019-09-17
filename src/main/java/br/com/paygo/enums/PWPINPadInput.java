package br.com.paygo.enums;

import br.com.paygo.exceptions.InvalidPINPadInputEventType;

import java.util.Arrays;

/**
 * Tipos de evento a serem ativados para monitoração no PIN-pad
 */
public enum PWPINPadInput {
    MAGSTRIPE(1),   // Foi passado um cartão magnético
    ICC(2),   // Foi detectada a presença de um cartão com chip
    CTLS(3),   // Foi detectada a presença de um cartão sem contato
    KEYCONF(17),    // Foi pressionada a tecla [OK]
    KEYBACKSP(18),  // Foi pressionada a tecla [CORRIGE]
    KEYCANC(19),    // Foi pressionada a tecla [CANCELA]
    KEYF1(33),      // Foi pressionada a tecla [F1]
    KEYF2(34),      // Foi pressionada a tecla [F2]
    KEYF3(35),      // Foi pressionada a tecla [F3]
    KEYF4(36);      // Foi pressionada a tecla [F4]

    private final int value;

    PWPINPadInput(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PWPINPadInput valueOf(int value) throws InvalidPINPadInputEventType {
        return Arrays.stream(values()).filter(pwPinPadInputEvent -> pwPinPadInputEvent.value == value).findFirst().orElseThrow(() -> new InvalidPINPadInputEventType("Dado do tipo PWPINPadInputEvent não mapeado (" + value + ")."));
    }
}
