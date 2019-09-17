package br.com.paygo.interop;

import br.com.paygo.enums.PWPINPadInput;
import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import com.sun.jna.ptr.LongByReference;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PINPad {

    private static final PINPad INSTANCE = new PINPad();
    private Map<String, String> pinPadMenu = new LinkedHashMap<>();

    private PINPad() {}

    public static PINPad getInstance() {
        return INSTANCE;
    }

    public String getMenuSelection(PWMenu menu, int numOptions, byte[] displayMessage) throws Exception {
        pinPadMenu = new LinkedHashMap<>();
        Field[] menuFields = menu.getDeclaredFields();

        try {
            for (int i = 0; i < numOptions; i++) {
                String label = new String((byte[]) menuFields[i].get(menu)).trim();
                pinPadMenu.put(new String((byte[]) menuFields[40 + i].get(menu)).trim(), "F" + (i+1) + "-" + label);
            }
        } catch (IllegalAccessException e) {
            System.out.println("-- ERRO AO EXIBIR OS DADOS --");
        }

        this.displayMenu();

        return this.getInput(displayMessage);
    }

    public PWRet displayMessage(String message) {
        try {
            PWRet ret = LibFunctions.showMessageOnPINPad(message);

            if (ret != PWRet.OK) {
                return PWRet.PINPADERR;
            }

            return ret;
        } catch (InvalidReturnTypeException e) {
            return PWRet.PINPADERR;
        }
    }

    private PWRet displayMenu() {
        StringBuilder formattedMenu = new StringBuilder();

        for(Map.Entry<String, String> entry : pinPadMenu.entrySet()) {
            formattedMenu.append(" " + entry.getValue() + " ");
        }

        try {
            PWRet ret = LibFunctions.showMessageOnPINPad(formattedMenu.toString());

            if (ret != PWRet.OK) {
                return PWRet.PINPADERR;
            }

            return ret;
        } catch (InvalidReturnTypeException e) {
            return PWRet.PINPADERR;
        }
    }

    public String getInput(byte[] displayMessage) throws Exception {
        LongByReference event = new LongByReference(1);
        List<String> opcoesMenu = new LinkedList<>(pinPadMenu.keySet());

        PWRet ret = LibFunctions.waitEventOnPINPad(event);

        if (ret != PWRet.OK) {
            throw new Exception("Erro no wait event do PIN-pad");
        }

        do {
            ret = LibFunctions.eventLoop(displayMessage);

            System.out.println(event.getValue());
            if (ret == PWRet.OK) {

                PWPINPadInput inputEvent = PWPINPadInput.valueOf((int)event.getValue());
                System.out.println(inputEvent);

                if (event.getValue() == PWPINPadInput.KEYCANC.getValue()) {
                    LibFunctions.abortTransaction();
                    throw new Exception("Transação cancelada!");
                }

                switch (inputEvent) {
                    case KEYF1:
                        return opcoesMenu.get(0);
                    case KEYF2:
                        return opcoesMenu.get(1);
                    case KEYF3:
                        if(opcoesMenu.size() >= 3) {
                            return opcoesMenu.get(3);
                        }
                        break;
                    case KEYF4:
                        if(opcoesMenu.size() >= 4) {
                            return opcoesMenu.get(4);
                        }
                        break;
                }

            }
        } while(true);
    }
}
