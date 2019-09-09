package br.com.paygo.gui;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.PWInfo;

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
    public void requestParam() {

    }

    @Override
    public void addParam(PWInfo param, String value) {
        pgWeb.addParam(param, value);
    }

    @Override
    public void logInfo(String message) {
        System.out.println(message);
    }

    @Override
    public void logParam(PWInfo param, String value) {
        System.out.println(param.name() + "(" + param.getValue() + ": " + value);
    }

    @Override
    public void showException(String message, boolean terminateApplication) {
        System.out.println(message);

        if (terminateApplication) {
            System.exit(-1);
        }
    }
}
