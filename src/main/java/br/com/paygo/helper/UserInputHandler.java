package br.com.paygo.helper;

import br.com.paygo.enums.PWValidDataEntry;
import br.com.paygo.interop.Menu;
import br.com.paygo.ui.UserInterface;

import java.util.ArrayList;
import java.util.Map;

public class UserInputHandler {

    public static String getTypedData(UserInterface userInterface, String promptMessage, int maxSize, int minSize, PWValidDataEntry validDataEntry) {
        return getTypedData(userInterface, promptMessage, (byte) maxSize, (byte) minSize, validDataEntry, "", "");
    }

    public static String getTypedData(UserInterface userInterface, String promptMessage, byte maxSize, byte minSize, PWValidDataEntry validDataEntry, String initialValue, String mascaraCaptura) {
        boolean isValid;
        String typedData;

        do {
            typedData = requestUserInput(userInterface, promptMessage, mascaraCaptura);

            isValid = validateUserInput(userInterface, typedData, maxSize, minSize, validDataEntry, initialValue);
        } while (!isValid);

        return typedData;
    }

    public static String requestSelectionFromMenu(UserInterface userInterface, Menu menu) {
        int userInput;
        Map<String, String> menuOptions = menu.build();

        do {
            userInput = userInterface.requestSelection("Selecione uma opção", new ArrayList<>(menuOptions.values()));
        } while (userInput > menu.getSize() || userInput < 0);

        return (String) menuOptions.keySet().toArray()[userInput];
    }

    private static String requestUserInput(UserInterface userInterface, String promptMessage, String mask) {
        return userInterface.requestParam("Informação Requerida", promptMessage, mask);

    }

    private static boolean validateUserInput(UserInterface userInterface, String typedData, byte maxSize, byte minSize,
                                             PWValidDataEntry validDataEntry, String initialValue) {
        if (typedData.length() > maxSize) {
            userInterface.alert("Tamanho maior que o maximo permitido (" + maxSize + ").\nTente novamente...");
            System.out.println();
        } else if (typedData.length() < minSize) {
            userInterface.alert("Tamanho menor que o mínimo permitido (" + minSize + ").\nTente novamente...");
            System.out.println();
        } else {
            switch (validDataEntry) {
                case INITIAL_VALUE:
                    System.out.println("entrada digitada está sendo sobrescrita por: " + initialValue);
                    return true;
                case NUMERIC:
                    if(typedData.matches("[0-9]+")) {
                        return true;
                    }
                    userInterface.alert("Digite apenas números");
                    break;
                case ALPHABETIC:
                    if(typedData.matches("[a-zA-Z]+")) {
                        return true;
                    }

                    userInterface.alert("Digite apenas alfabéticos");
                    break;
                case ALPHANUMERIC:
                    if(typedData.matches("[0-9a-zA-Z]+")) {
                        return true;
                    }

                    userInterface.alert("Digite apenas alfanuméricos");
                    break;
                case ALL:
                    System.out.println("aceitando numéricos, alfanuméricos e especiais");
                    return true;
            }
        }

        return false;
    }
}
