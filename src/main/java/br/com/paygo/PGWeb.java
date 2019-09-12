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

    public void version() {
        transaction = new Transaction(PWOper.VERSION, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n\n=> VERIFICAÇÃO DE VERSÃO CONCLUÍDA <=\n\n");
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
            userInterface.logInfo("\n\n=> REIMPRESSÃO CONCLUÍDA <=\n\n");
        }
    }

    public void saleVoid() {
        transaction = new Transaction(PWOper.SALEVOID, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n\n=> CANCELAMENTO DE VENDA CONCLUÍDO <=\n\n");
        }
    }

    public void reportTrunc() {
        this.generateReport(PWOper.RPTTRUNC);
    }

    public void reportDetail() {
        this.generateReport(PWOper.RPTDETAIL);
    }

    private void generateReport(PWOper reportType) {
        transaction = new Transaction(reportType, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            try {
                transaction.getResult(PWInfo.RCPTFULL);
                userInterface.logInfo("\t" + transaction.getValue(false) + "\n\n");

                userInterface.logInfo("\n\n=> RELATÓRIO CONCLUÍDO <=\n\n");
            } catch (InvalidReturnTypeException e) {
                userInterface.showException("Erro ao exibir o relatório de operações", false);
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
