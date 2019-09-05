package br.com.paygo.helper;

import br.com.paygo.enums.PWValidDataEntry;
import br.com.paygo.interop.Menu;

import java.util.Map;
import java.util.Scanner;

public class UserInputHandler {

    public static String getTypedData(String promptMessage, byte maxSize, byte minSize, PWValidDataEntry validDataEntry, String initialValue) {
        boolean isValid;
        String typedData;

        do {
            typedData = requestUserInput(promptMessage);

            isValid = validateUserInput(typedData, maxSize, minSize, validDataEntry, initialValue);
        } while (!isValid);

        return typedData;
    }

    public static String requestSelectionFromMenu(Menu menu) {
        Scanner scanner = new Scanner(System.in);
        int userInput;
        int menuIndex = 1;

        Map<String, String> menuOptions = menu.build();

        do {
            for (Map.Entry entry: menuOptions.entrySet()) {
                System.out.println("[" + menuIndex++ + "] " + entry.getValue() + " (" + entry.getKey() + ")");
            }

            System.out.println("SELECIONE UMA OPÇÃO:");
            userInput = scanner.nextInt() - 1;

        } while (userInput > menu.getSize() || userInput < 0);

        return (String) menuOptions.keySet().toArray()[userInput];
    }

    private static String requestUserInput(String promptMessage) {
        System.out.println("||| " + promptMessage + " |||");
        Scanner scan = new Scanner(System.in);

        return scan.nextLine();
    }

    private static boolean validateUserInput(String typedData, byte maxSize, byte minSize, PWValidDataEntry validDataEntry, String initialValue) {
        if (typedData.length() > maxSize) {
            System.out.println("Tamanho maior que o maximo permitido (" + maxSize + ")\nTente novamente...");
        } else if (typedData.length() < minSize) {
            System.out.println("Tamanho menor que o maximo permitido (" + minSize + ")\nTente novamente...");
        } else {
            switch (validDataEntry) {
                case INITIAL_VALUE:
                    System.out.println("entrada digitada está sendo sobrescrita por: " + initialValue);
                    typedData = initialValue.replace(" ","");
                    return true;
                case NUMERIC:
                    if(typedData.matches("[0-9]+")) {
                        return true;
                    }

                    System.out.println("Digite apenas números");
                    break;
                case ALPHABETIC:
                    if(typedData.matches("[a-zA-Z]+")) {
                        return true;
                    }

                    System.out.println("Digite apenas alfabéticos");
                case ALPHANUMERIC:
                    if(typedData.matches("[0-9a-zA-Z]+")) {
                        return true;
                    }

                    System.out.println("Digite apenas alfanuméricos");
                case ALL:
                    System.out.println("aceitando numéricos, alfanuméricos e especiais");
                    return true;
            }
        }

        return false;
    }
}
