package br.com.paygo;

import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import br.com.paygo.interop.LibFunctions;
import br.com.paygo.interop.Transaction;
import br.com.paygo.ui.UserInterface;

public class PGWeb {

    private final UserInterface userInterface;
    private static final String PATH = ".";
    private Transaction transaction;

    public PGWeb(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void init() {
        try {
            PWRet returnedCode = LibFunctions.init(PATH);
            userInterface.logInfo("=> PW_iInit: " + returnedCode.toString());
        } catch (Exception e) {
            userInterface.showException("Erro ao iniciar a comunicação.", true);
        }
    }

    public void install() {
        transaction = new Transaction(PWOper.INSTALL, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n\n=> INSTALAÇÃO CONCLUÍDA <=\n\n");
        }
    }

    public void sale() {
        transaction = new Transaction(PWOper.SALE, userInterface);

        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n\n=> VENDA CONCLUÍDA <=\n\n");
        }
    }

    public void reprint() {
        transaction = new Transaction(PWOper.REPRINT, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            try {
                transaction.getResult(PWInfo.RCPTMERCH);
                userInterface.logInfo("------ REIMPRESSÃO - VIA ESTABELECIMENTO ------");
                userInterface.logInfo("\t" + transaction.getValue(false) + "\n\n");

                transaction.getResult(PWInfo.RCPTCHOLDER);

                userInterface.logInfo("------ REIMPRESSÃO - VIA CLIENTE ------");
                userInterface.logInfo("\t" + transaction.getValue(false) + "\n\n");

                userInterface.logInfo("\n\n=> REIMPRESSÃO CONCLUÍDA <=\n\n");
            } catch (InvalidReturnTypeException e) {
                userInterface.showException(e.getMessage(), false);
            }
        }
    }

    public void abort() {
        try {
            this.transaction.abort();
        } catch (InvalidReturnTypeException e) {
            userInterface.showException(e.getMessage(), true);
        }
    }
}
