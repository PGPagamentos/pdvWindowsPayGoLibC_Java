package br.com.paygo;

import com.sun.jna.Native;
import com.sun.jna.Platform;

class PGWebLib {

    private static final String libName = Platform.isLinux() ? "PGWebLib.so" : "PGWebLib.dll";
    private final PGWebLibMap libInterface;

    static {
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.debug_load.jna", "true");
        System.setProperty("jna.platform.library.path", "/usr/local");
    }

    PGWebLib() {
        this.libInterface = Native.load(libName, PGWebLibMap.class);
    }

    short init(String path) {
        return libInterface.PW_iInit(path);
    }
}
