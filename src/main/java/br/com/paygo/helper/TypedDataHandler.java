package br.com.paygo.helper;

import java.util.Scanner;

public class TypedDataHandler {

    public static String getTypedData(String promptMessage, byte maxSize, byte minSize, byte validDataEntries, String initialValue) {
        boolean isValid;
        String typedData;

        do {
            typedData = requestUserInput(promptMessage);

            isValid = validateUserInput(typedData, maxSize, minSize, validDataEntries, initialValue);
        } while (!isValid);

        return typedData;
    }

    private static String requestUserInput(String promptMessage) {

        System.out.println("||| " + promptMessage + " |||");
        Scanner scan = new Scanner(System.in);
        String userInput = scan.nextLine();

        return userInput;
    }

    private static boolean validateUserInput(String typedData, byte maxSize, byte minSize, byte validDataEntries, String initialValue) {
        if (typedData.length() > maxSize) {
            System.out.println("Tamanho maior que o maximo permitido(" + maxSize + ")\nTente novamente...");
        } else if (typedData.length() < minSize) {
            System.out.println("Tamanho menor que o maximo permitido(" + minSize + ")\nTente novamente...");
        } else {
            switch (validDataEntries) {
                case 0: //VALOR INICIAL SOBREPÕE
                    System.out.println("entrada digitada está sendo sobrescrita por: " + initialValue);
                    typedData = initialValue.replace(" ","");
                    return true;
                case 1: //SÓ NUMEROS
                    if(typedData.matches("[0-9]+")) {
                        return true;
                    }

                    System.out.println("Digite apenas numeros");
                    break;
                case 2: // SÓ ALFABÉTICOS
                    if(typedData.matches("[a-zA-Z]+")) {
                        return true;
                    }

                    System.out.println("Digite apenas alfabéticos");
                case 3: //SÓ ALFANUMERICOS
                    if(typedData.matches("[0-9a-zA-Z]+")) {
                        return true;
                    }

                    System.out.println("Digite apenas alfanuméricos");
                case 7: //ALFANUMÉRICOS E ESPECIAS
                    System.out.println("aceitando numericos, alfanumericos e especiais");
                    return true;
            }
        }

        return false;
    }
}
