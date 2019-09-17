package br.com.paygo;

import br.com.paygo.enums.*;
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
            transaction.printReceipt();

            userInterface.logInfo("\n\n=> VENDA CONCLUÍDA <=\n\n");
        }
    }

    public void saleOnPINPad() {
        try {
            transaction = new Transaction(PWOper.SALE, userInterface);
            PWRet returnedCode = transaction.initInteractionOnPINPad();

            if(returnedCode == PWRet.OK) {
                returnedCode = transaction.executeOperation();

                if (returnedCode == PWRet.OK) {
                    transaction.printReceipt();

                    userInterface.logInfo("\n\n=> AUTO ATENDIMENTO CONCLUÍDO <=\n\n");
                }
            }
        } catch (Exception e) {
            userInterface.showException(e.getMessage(), true);
        }
    }

    public void reprint() {
        transaction = new Transaction(PWOper.REPRINT, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            transaction.printReceipt();

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

    public void checkPendingConfirmation() {
        try {
            transaction = new Transaction(PWOper.PNDCNF, userInterface);

            Confirmation confirmation = new Confirmation(transaction);
            confirmation.executeConfirmationProcess(true);

            transaction.printReceipt();

            transaction.abort();
        } catch (Exception e) {
            userInterface.showException(e.getMessage(), false);
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

    public String requestDataOnPinPad(PWData data, PWUserDataMessage message, int minSize, int maxSize) {
        PWRet ret;
        String key = "12345678901234567890123456789012";
        byte[] response = new byte[1000];

        try {
            if (minSize > maxSize) {
                throw new Exception("Tamanho máximo deve ser maior que o tamanho mínimo!");
            }

            if (data == PWData.PPENTRY) {
                ret = LibFunctions.getUserDataOnPINPad(message, minSize, maxSize, 30, response);
            } else {
                if(minSize < 4) {
                    throw new Exception("Tamanho mínimo deve ser maior que 4!");
                }

                String promptMessage = "TESTE DE CAPTURA\nDE PIN BLOCK";
                ret = LibFunctions.getPINBlock(key, minSize, maxSize, 30, promptMessage, response);
            }

            if (ret != PWRet.OK) {
                userInterface.showException("Erro ao solicitar dado via PIN-pad", false);
            }
        } catch (Exception e) {
            userInterface.showException(e.getMessage(), false);
        }

        return new String(response);
    }

    public void abort() {
        try {
            this.transaction.abort();
            userInterface.logInfo("\n\n EXECUÇÃO ABORTADA PELO USUÁRIO! \n\n");
        } catch (InvalidReturnTypeException e) {
            userInterface.showException(e.getMessage(), true);
        }
    }
}
