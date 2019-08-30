package br.com.paygo.enums;

/**
 * Tipos de evento retornados pelo PIN-pad
 */
public enum PWPINPadEvent {
    MAGSTRIPE(0x01), // Foi passado um cartão magnético. 
    ICC(0x02), //Foi detectada a presença de um cartão com chip. 
    CTLS(0x03), //Foi detectada a presença de um cartão sem contato. 
    KEYCONF(0x11), //Foi pressionada a tecla [OK]. 
    KEYBACKSP(0x12), //Foi pressionada a tecla [CORRIGE]. 
    KEYCANC(0x13), //Foi pressionada a tecla [CANCELA]. 
    KEYF1(0x21), //Foi pressionada a tecla [F1]. 
    KEYF2(0x22), //Foi pressionada a tecla [F2]. 
    KEYF3(0x23), //Foi pressionada a tecla [F3]. 
    KEYF4(0x24); //Foi pressionada a tecla [F4].

    private final int value;

    PWPINPadEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
