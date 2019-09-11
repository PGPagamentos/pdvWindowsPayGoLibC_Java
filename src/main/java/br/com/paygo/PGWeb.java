package br.com.paygo;

import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import br.com.paygo.interop.Confirmation;
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
        PWRet returnedCode;

        try {
            transaction = new Transaction(PWOper.INSTALL, userInterface);
            returnedCode = transaction.start();

            if (returnedCode == PWRet.OK) {
                do {
                    returnedCode = transaction.executeTransaction();
                    userInterface.logInfo("=> PW_iExecTransac: " + returnedCode);

                    if (returnedCode == PWRet.MOREDATA) {
                        transaction.retrieveMoreData();
                    }
                } while (returnedCode == PWRet.MOREDATA);

                if (returnedCode == PWRet.OK) {
                    userInterface.logInfo("\n\n=> INSTALAÇÃO CONCLUÍDA <=\n\n");
                }
            } else {
                returnedCode = transaction.getResult(PWInfo.RESULTMSG);
                userInterface.logInfo("=> PW_iGetResult: " + returnedCode + "\n\t" + transaction.getValue(true));
            }
        } catch (Exception e) {
            userInterface.showException(e.getMessage(), true);
        }
    }

    public void sale() {
        PWRet returnedCode;

        try {
            transaction = new Transaction(PWOper.SALE, userInterface);
            returnedCode = transaction.start();

            if (returnedCode == PWRet.OK) {
                do {
                    returnedCode = transaction.executeTransaction();
                    userInterface.logInfo("=> PW_iExecTransac: " + returnedCode);

                    if (returnedCode == PWRet.MOREDATA) {
                        transaction.retrieveMoreData();
                    }
                } while (returnedCode == PWRet.MOREDATA);

                if (returnedCode == PWRet.OK) {
                    returnedCode = transaction.finalizeTransaction();

                    if (returnedCode == PWRet.OK) {
                        userInterface.logInfo("\n\n=> VENDA CONCLUÍDA <=\n\n");
                    }
                } else if(returnedCode == PWRet.REQPARAM) {
                    userInterface.showException("Falha de comunicação com a infraestrutura do Pay&Go Web (falta parâmetro obrigatório).", false);
                } else if(returnedCode == PWRet.FROMHOSTPENDTRN) {
                    System.out.println("===========================================" +
                            "== ERRO - EXITSTE UMA TRANSAÇÃO PENDENTE ==" +
                            "===========================================");
                    Confirmation confirmation = new Confirmation(transaction);
                    returnedCode = confirmation.executeConfirmationProcess(true);

                    userInterface.logInfo("=> PW_iConfirmation: " + returnedCode);

                    if (returnedCode == PWRet.OK) {
                        userInterface.logInfo("\n\n=> CONFIRMAÇÃO PENDENTE CONCLUÍDA <=\n\n");
                    }
                } else if(returnedCode == PWRet.PINPADERR) {
                    userInterface.showException("Erro de comunição com o PIN-pad", false);
                }
            } else {
                returnedCode = transaction.getResult(PWInfo.RESULTMSG);
                userInterface.logInfo("=> PW_iGetResult: " + returnedCode + "\n\t" + transaction.getValue(true));
            }
        } catch (Exception e) {
            userInterface.showException(e.getMessage(), true);
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
