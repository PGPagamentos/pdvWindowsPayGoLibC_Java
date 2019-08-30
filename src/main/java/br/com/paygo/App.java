package br.com.paygo;

public class App 
{
    private static final String path = "./";

    public static void main(String[] args) {
        PGWebLib pgWebLib = new PGWebLib();
        short returnedCode;

        returnedCode = pgWebLib.init(path);

        System.out.println("=> PW_iInit: " + returnedCode);
    }
}
