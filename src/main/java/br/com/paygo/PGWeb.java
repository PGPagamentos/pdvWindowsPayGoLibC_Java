package br.com.paygo;

import br.com.paygo.enums.*;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.interop.Confirmation;
import br.com.paygo.interop.LibFunctions;
import br.com.paygo.interop.Transaction;
import br.com.paygo.ui.UserInterface;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe responsável por fazer a interface entre os métodos da biblioteca PGWebLib e a interface de usuário
 */
public class PGWeb {

    private final UserInterface userInterface;
    private static final String PATH = ".";
    private Transaction transaction;

    public static LinkedHashMap<PWInfo, String> confirmData = new LinkedHashMap<>();

    public PGWeb(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void init() {
        try {
            userInterface.logInfo("=== INICIALIZAÇÃO DA BIBLIOTECA ===\n");
            PWRet returnedCode = LibFunctions.init(PATH);
            userInterface.logInfo("=> PW_iInit: " + returnedCode.toString());
        } catch (Exception e) {
            userInterface.showException("Erro ao iniciar a comunicação.", true);
        }
    }

    public void version() {
        userInterface.logInfo("\n=== VERIFICAÇÃO DE VERSÃO DA DLL ===\n");
        transaction = new Transaction(PWOper.VERSION, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n=== VERIFICAÇÃO DE VERSÃO DA DLL CONCLUÍDA ===\n");
        }
    }

    public void install() {
        userInterface.logInfo("\n=== INSTALAÇÃO ===\n");
        transaction = new Transaction(PWOper.INSTALL, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n=== INSTALAÇÃO CONCLUÍDA ===\n");
        }

        transaction.printResultParams();
    }

    public void admin() {
        transaction = new Transaction(PWOper.ADMIN, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n=== ADMINISTRATIVO CONCLUÍDO ===\n");
        }
    }

    public void sale() {
        userInterface.logInfo("\n=== VENDA ===\n");

        transaction = new Transaction(PWOper.SALE, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n=> VENDA CONCLUÍDA <=\n");
        }

        transaction.printResultParams();
    }

    public void saleOnPINPad() {
        userInterface.logInfo("\n=== AUTO ATENDIMENTO ===\n");
        try {
            transaction = new Transaction(PWOper.SALE, userInterface);
            PWRet returnedCode = transaction.initInteractionOnPINPad();

            if(returnedCode == PWRet.OK) {
                returnedCode = transaction.executeOperation();

                if (returnedCode == PWRet.OK) {
                    userInterface.logInfo("\n=== AUTO ATENDIMENTO CONCLUÍDO ===\n");
                }
            } else {
                userInterface.logInfo("\n=== AUTO ATENDIMENTO CANCELADO ===\n");
            }
        } catch (Exception e) {
            userInterface.showException(e.getMessage(), true);
        }

        transaction.printResultParams();
    }

    public void reprint() {
        userInterface.logInfo("\n=== REIMPRESSÃO ===\n");
        transaction = new Transaction(PWOper.REPRINT, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n=== REIMPRESSÃO CONCLUÍDA ===\n");
        }

        transaction.printResultParams();
    }

    public void saleVoid() {
        userInterface.logInfo("\n=== CANCELAMENTO DE VENDA ===\n");
        transaction = new Transaction(PWOper.SALEVOID, userInterface);
        PWRet returnedCode = transaction.executeOperation();

        if (returnedCode == PWRet.OK) {
            userInterface.logInfo("\n=== CANCELAMENTO DE VENDA CONCLUÍDO ===\n");
        }

        transaction.printResultParams();
    }

    public void checkPendingConfirmation() {
        userInterface.logInfo("\n=== CONFIRMAÇÃO ===\n");

        logConfirmData();

        try {
            Confirmation confirmation = new Confirmation(userInterface, PGWeb.confirmData);
            PWRet ret = confirmation.executeConfirmationProcess();
            byte[] value = new byte[1000];

            LibFunctions.getResult(PWInfo.RESULTMSG, value);
            userInterface.logInfo(PWInfo.RESULTMSG + "<0X" + Integer.toHexString(PWInfo.CNFREQ.getValue()) + "> = " + TextFormatter.formatByteMessage(value));

            if (ret == PWRet.OK) {
                userInterface.logInfo("Confirmação OK");
            }
        } catch (Exception e) {
            userInterface.showException(e.getMessage(), false);
        }

        userInterface.logInfo("\n=== CONFIRMAÇÃO CONCLUÍDA ===\n");
    }

    private void logConfirmData() {
        userInterface.logInfo("\n=== PARAM CONFIRMACAO ===");

        for (Map.Entry<PWInfo, String> entry: confirmData.entrySet()) {
            userInterface.logInfo(entry.getKey() + " = " + entry.getValue());
        }

        userInterface.logInfo("=========================\n");
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
            userInterface.logInfo("\n=== RELATÓRIO ===\n");
            byte[] value = new byte[1000];
            LibFunctions.getResult(PWInfo.RCPTFULL, value);

            userInterface.logInfo(new String(value));

            userInterface.logInfo("\n=== RELATÓRIO CONCLUÍDO ===\n");
        }

        transaction.printResultParams();
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
        this.transaction.abort(false);
        userInterface.logInfo("\n\n EXECUÇÃO CANCELADA PELO USUÁRIO! \n\n");
    }
}
