package br.com.paygo.interop;

import com.sun.jna.Structure;

public class PWOperation extends Structure {

    byte type;
    byte[] name = new byte[21];
    byte[] value = new byte[21];
}
