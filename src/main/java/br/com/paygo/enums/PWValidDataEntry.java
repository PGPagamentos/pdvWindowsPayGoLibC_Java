package br.com.paygo.enums;

import java.util.Arrays;

public enum PWValidDataEntry {
    INITIAL_VALUE(0),
    NUMERIC(1),
    ALPHABETIC(2),
    ALPHANUMERIC(3),
    ALL(7);

    private final int value;

    PWValidDataEntry(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PWValidDataEntry valueOf(short value) {
        return Arrays.stream(values()).filter(pwValidDataEntry -> pwValidDataEntry.value == value).findFirst().orElse(ALL);
    }
}
