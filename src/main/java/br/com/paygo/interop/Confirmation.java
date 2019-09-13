package br.com.paygo.interop;

import br.com.paygo.enums.PWCnf;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe responsável por realizar a confirmação de uma transação.
 * Se forem passados todos os parâmetros para o objeto, ele irá realizar a confirmação da última transação.
 * Se não forem passados os parâmetros para o objeto, ele irá realizar a confirmação de uma transação pendente.
 */
public class Confirmation {

    private LinkedHashMap<PWInfo, String> confirmationParams;
    private final Transaction transaction;
    private final List<PWInfo> requiredPendingParams = new LinkedList<PWInfo>() {{
        add(PWInfo.PNDREQNUM);
        add(PWInfo.PNDAUTLOCREF);
        add(PWInfo.PNDAUTEXTREF);
        add(PWInfo.PNDVIRTMERCH);
        add(PWInfo.PNDAUTHSYST);
    }};

    public Confirmation(Transaction transaction) {
        this.transaction = transaction;
        this.confirmationParams = new LinkedHashMap<>();
    }

    Confirmation(Transaction transaction, LinkedHashMap<PWInfo, String> confirmationParams) {
        this.transaction = transaction;
        this.confirmationParams = confirmationParams;
    }

    public PWRet executeConfirmationProcess(boolean pendingTransaction) throws InvalidReturnTypeException {
        PWCnf confirmationType;

        if (!pendingTransaction && !confirmationParams.isEmpty()) {
            confirmationType = PWCnf.AUTO;
        } else {
            transaction.getUserInterface().logInfo("\n======== CONFIRMAÇÃO PENDENTE ========");

            confirmationType = retrieveConfirmationType();
            int actionSelected = retrieveAction();

            if (actionSelected == 0) {
                if(confirmationParams.isEmpty()) {
                    transaction.getUserInterface().showException("Não é possível buscar automaticamente os dados da transação pendente neste contexto.", false);
                    return PWRet.FROMHOSTTRNNFOUND;
                } else {
                    getConfirmationData();
                }
            } else {
                retrieveConfirmationData();
            }
        }

        return LibFunctions.confirmTransaction(confirmationType, new LinkedList<>(confirmationParams.values()));
    }

    /**
     * Solicitação ao usuário o tipo de confirmação que deve ser realizada.
     */
    private PWCnf retrieveConfirmationType() {
        int selectedOption;
        ArrayList<String> confirmationTypeOptions = new ArrayList<>();

        for (PWCnf pwCnf : PWCnf.values()) {
            confirmationTypeOptions.add(pwCnf.toString());
        }

        do {
            selectedOption = transaction.getUserInterface().requestSelection("Selecione o tipo de confirmação", confirmationTypeOptions);
        } while (selectedOption < 0 || selectedOption > 9);

        return PWCnf.values()[selectedOption];
    }

    /**
     * Solicita ao usuário a forma que deseja realizar a confirmação da transação pendente.
     */
    private int retrieveAction() {
        int selectedOption;
        ArrayList<String> actions = new ArrayList<>();

        actions.add("Confirmar última transação");
        actions.add("Informar dados manualmente");

        do {
            selectedOption = transaction.getUserInterface().requestSelection("Ação", actions);
        } while(selectedOption < 0 || selectedOption > 1);

        return selectedOption;
    }

    /**
     * Solicita ao usuários os parâmetros de uma transação pendente.
     */
    private void retrieveConfirmationData() {
        for (PWInfo info : requiredPendingParams) {
            String response = transaction.getUserInterface().requestParam("Parâmetros da transação pendente", info.toString(), "");
            confirmationParams.put(info, response);
        }
    }

    /**
     * Busca na PGW os parâmetros de uma transação pendente.
     */
    private void getConfirmationData() throws InvalidReturnTypeException {
        byte[] value;

        transaction.getUserInterface().logInfo("Buscando automaticamente os parâmetros de confirmação da transação pendente.");

        for (PWInfo info : requiredPendingParams) {
            value = new byte[1000];

            LibFunctions.getResult(info, value);

            transaction.getUserInterface().logInfo(info + " - " + new String(value));

            confirmationParams.put(info, new String(value));
        }
    }
}
