package br.com.paygo.ui;

import br.com.paygo.enums.PWInfo;

import java.util.ArrayList;
import java.util.HashMap;

public interface UserInterface {
    void init();
    void install();
    void sale();
    void abort();
    String requestParam(String title, String message, String mask);
    int requestSelection(String title, ArrayList<String> options);
    void alert(String message);
    void logInfo(String message);
    void showException(String message, boolean terminateApplication);
    HashMap<PWInfo, String> getParams();
}
