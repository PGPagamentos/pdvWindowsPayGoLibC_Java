package br.com.paygo;

import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;

public class App
{
    private static final String path = "./";

    public static void main(String[] args) {
        PGWebLib pgWebLib = new PGWebLib();
        short returnedCode;

        returnedCode = pgWebLib.init(path);

        System.out.println("=> PW_iInit: " + returnedCode);

        returnedCode = pgWebLib.newTransaction(PWOper.INSTALL);

        System.out.println("=> PW_iNewTransac: " + returnedCode);

        returnedCode = pgWebLib.addParam(PWInfo.AUTNAME, "Teste");

        System.out.println("=> PW_iAddParam: " + returnedCode);
    }
}
