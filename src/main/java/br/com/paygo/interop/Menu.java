package br.com.paygo.interop;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class Menu {

    private final PWGetData getData;
    private final int size;

    public Menu(PWGetData getData) {
        this.getData = getData;
        this.size = getData.getNumOpcoesMenu();
    }

    public Map<String, String> build() {
        Map<String, String> menu = new LinkedHashMap<>();
        PWMenu pwMenu = getData.getMenu();
        Field[] menuFields = pwMenu.getDeclaredFields();

        try {
            for (int i = 0; i < size; i++) {
                menu.put(new String((byte[]) menuFields[40 + i].get(pwMenu)).trim(), new String((byte[]) menuFields[i].get(pwMenu)).trim());
            }
        } catch (IllegalAccessException e) {
            System.out.println("-- ERRO AO EXIBIR OS DADOS --");
        }

        return menu;
    }

    public int getSize() {
        return size;
    }
}
