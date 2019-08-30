package br.com.paygo;

import com.sun.jna.Library;

public interface PGWebLibMap extends Library {

    short PW_iInit(String workingDir);
}
