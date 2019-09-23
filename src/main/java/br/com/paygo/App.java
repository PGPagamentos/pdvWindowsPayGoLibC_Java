package br.com.paygo;

import br.com.paygo.ui.SwingInterface;
import br.com.paygo.ui.UserInterface;

public class App
{
    /**
     * Deve ser instanciado tipo de interface desejado para interagir com a aplicação
     * gráfica = SwingInterface.java
     * cmd = CMDInterface.java
     */
    public static void main(String[] args) {
        UserInterface userInterface = new SwingInterface();
//        UserInterface userInterface = new CMDInterface();
    }
}
