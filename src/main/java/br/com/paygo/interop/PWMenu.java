package br.com.paygo.interop;

import com.sun.jna.Structure;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class PWMenu extends Structure {
    
    public byte[] text1 = new byte[41];
    public byte[] text2 = new byte[41];
    public byte[] text3 = new byte[41];
    public byte[] text4 = new byte[41];
    public byte[] text5 = new byte[41];
    public byte[] text6 = new byte[41];
    public byte[] text7 = new byte[41];
    public byte[] text8 = new byte[41];
    public byte[] text9 = new byte[41];
    public byte[] text10 = new byte[41];
    public byte[] text11 = new byte[41];
    public byte[] text12 = new byte[41];
    public byte[] text13 = new byte[41];
    public byte[] text14 = new byte[41];
    public byte[] text15 = new byte[41];
    public byte[] text16 = new byte[41];
    public byte[] text17 = new byte[41];
    public byte[] text18 = new byte[41];
    public byte[] text19 = new byte[41];
    public byte[] text20 = new byte[41];
    public byte[] text21 = new byte[41];
    public byte[] text22 = new byte[41];
    public byte[] text23 = new byte[41];
    public byte[] text24 = new byte[41];
    public byte[] text25 = new byte[41];
    public byte[] text26 = new byte[41];
    public byte[] text27 = new byte[41];
    public byte[] text28 = new byte[41];
    public byte[] text29 = new byte[41];
    public byte[] text30 = new byte[41];
    public byte[] text31 = new byte[41];
    public byte[] text32 = new byte[41];
    public byte[] text33 = new byte[41];
    public byte[] text34 = new byte[41];
    public byte[] text35 = new byte[41];
    public byte[] text36 = new byte[41];
    public byte[] text37 = new byte[41];
    public byte[] text38 = new byte[41];
    public byte[] text39 = new byte[41];
    public byte[] text40 = new byte[41];
    public byte[] value1 = new byte[256];
    public byte[] value2 = new byte[256];
    public byte[] value3 = new byte[256];
    public byte[] value4 = new byte[256];
    public byte[] value5 = new byte[256];
    public byte[] value6 = new byte[256];
    public byte[] value7 = new byte[256];
    public byte[] value8 = new byte[256];
    public byte[] value9 = new byte[256];
    public byte[] value10 = new byte[256];
    public byte[] value11 = new byte[256];
    public byte[] value12 = new byte[256];
    public byte[] value13 = new byte[256];
    public byte[] value14 = new byte[256];
    public byte[] value15 = new byte[256];
    public byte[] value16 = new byte[256];
    public byte[] value17 = new byte[256];
    public byte[] value18 = new byte[256];
    public byte[] value19 = new byte[256];
    public byte[] value20 = new byte[256];
    public byte[] value21 = new byte[256];
    public byte[] value22 = new byte[256];
    public byte[] value23 = new byte[256];
    public byte[] value24 = new byte[256];
    public byte[] value25 = new byte[256];
    public byte[] value26 = new byte[256];
    public byte[] value27 = new byte[256];
    public byte[] value28 = new byte[256];
    public byte[] value29 = new byte[256];
    public byte[] value30 = new byte[256];
    public byte[] value31 = new byte[256];
    public byte[] value32 = new byte[256];
    public byte[] value33 = new byte[256];
    public byte[] value34 = new byte[256];
    public byte[] value35 = new byte[256];
    public byte[] value36 = new byte[256];
    public byte[] value37 = new byte[256];
    public byte[] value38 = new byte[256];
    public byte[] value39 = new byte[256];
    public byte[] value40 = new byte[256];

    public PWMenu() {}

    Field[] getDeclaredFields() {
        return this.getClass().getDeclaredFields();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("text1", "text2", "text3", "text4", "text5", "text6", "text7", "text8", "text9", "text10",
                "text11", "text12", "text13", "text14", "text15", "text16", "text17", "text18", "text19", "text20",
                "text21", "text22", "text23", "text24", "text25", "text26", "text27", "text28", "text29", "text30",
                "text31", "text32", "text33", "text34", "text35", "text36", "text37", "text38", "text39", "text40",
                "value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10",
                "value11", "value12", "value13", "value14", "value15", "value16", "value17", "value18", "value19", "value20",
                "value21", "value22", "value23", "value24", "value25", "value26", "value27", "value28", "value29", "value30",
                "value31", "value32", "value33", "value34", "value35", "value36", "value37", "value38", "value39", "value40");
    }
}
