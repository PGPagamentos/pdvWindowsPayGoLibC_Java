package br.com.paygo.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter {

    public static String formatByteMessage(byte[] param) {
        String value = new String (param); // transaformando array de bytes em string
        String formattedValue = value.replace('\0',' '); //substituindo byte vazio por espaços

        Pattern p = Pattern.compile("\\s+"); // expressão para mais de um espaço
        Matcher m = p.matcher(formattedValue);
        value = m.replaceAll(" ");

        return value.substring(0,value.length()-1);
    }
}
