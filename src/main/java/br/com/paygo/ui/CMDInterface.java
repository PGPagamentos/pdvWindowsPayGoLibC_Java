package br.com.paygo.ui;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.PWInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CMDInterface implements UserInterface {

    private PGWeb pgWeb;

    public CMDInterface() {
        this.pgWeb = new PGWeb(this);
    }

    @Override
    public void init() {
        pgWeb.init();
    }

    @Override
    public void install() {
        pgWeb.install();
    }

    @Override
    public void sale() {
        pgWeb.sale();
    }

    @Override
    public void abort() {
        logInfo("\n\n EXECUÇÃO ABORTADA PELO USUÁRIO! \n\n");
        pgWeb.abort();
    }

    @Override
    public String requestParam(String title, String message, String mask) {
        System.out.println("||| " + title + " |||");
        System.out.println(message);
        Scanner scan = new Scanner(System.in);

        return scan.nextLine();
    }

    @Override
    public int requestSelection(String title, ArrayList<String> options) {
        Scanner scanner = new Scanner(System.in);
        int menuIndex = 1;

        for (String option : options) {
            System.out.println("[" + menuIndex++ + "] " + option);
        }

        System.out.println("SELECIONE UMA OPÇÃO:");
        return scanner.nextInt() - 1;
    }

    @Override
    public void alert(String message) {
        System.out.println(message);
    }

    @Override
    public void logInfo(String message) {
        System.out.println(message);
    }

    @Override
    public void showException(String message, boolean terminateApplication) {
        System.out.println(message);

        if (terminateApplication) {
            System.exit(-1);
        }
    }

    @Override
    public HashMap<PWInfo, String> getParams() {
        return new HashMap<>();
    }
}
