package br.com.paygo.gui;

import br.com.paygo.enums.PWInfo;

public interface UserInterface {

    void init();
    void install();
    void requestParam();
    void addParam(PWInfo param, String value);
    void logInfo(String message);
    void logParam(PWInfo param, String value);
    void showException(String message, boolean terminateApplication);
}
