package br.com.paygo.interop;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.PWCnf;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWRet;
import br.com.paygo.ui.UserInterface;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe responsável por realizar a confirmação de uma transação.
 *
 */
public class Confirmation {

    private LinkedHashMap<PWInfo, String> confirmationParams;
    private final UserInterface userInterface;
    @SuppressWarnings("serial")
	private static final List<PWInfo> requiredConfirmationParams = new LinkedList<PWInfo>() {{
        add(PWInfo.REQNUM);
        add(PWInfo.AUTLOCREF);
        add(PWInfo.AUTEXTREF);
        add(PWInfo.VIRTMERCH);
        add(PWInfo.AUTHSYST);
    }};
    @SuppressWarnings("serial")
	private static final List<PWInfo> requiredPendingConfirmationParams = new LinkedList<PWInfo>() {{
        add(PWInfo.PNDREQNUM);
        add(PWInfo.PNDAUTLOCREF);
        add(PWInfo.PNDAUTEXTREF);
        add(PWInfo.PNDVIRTMERCH);
        add(PWInfo.PNDAUTHSYST);
    }};

    public Confirmation(UserInterface userInterface, LinkedHashMap<PWInfo, String> confirmationParams) {
        this.userInterface = userInterface;
        this.confirmationParams = confirmationParams;
    }

    public PWRet executeConfirmationProcess() {
        PWCnf confirmationType = retrieveConfirmationType();
        int actionSelected = 0;
        
        /* Define e obtém como a transação vai ser confirmada o desfeita
         * Esse processo pode ser feito de forma automática pela automação, ou seja,
         * Em determinados pontos da aplicação são marcados os desfazimentos e, caso não ocorra
         * nenhum tipo de erro, por exemplo falha na impressão, queda de energia, entre outros, 
         * a transação é confirmada automaticamente. 
         * Lembrando que esse comportamento não está implementado, é uma sugestão as automações!*/
        if (confirmationType == null) {
            return PWRet.CANCEL;
        }
        actionSelected = retrieveAction();

        // Caso os dados da confirmação/desfazimento forem inseridos manualmente
        if (actionSelected == 1) {
            retrieveConfirmationData();
        }
        // Verifica se foram capturados os parâmetros de confirmação/desfazimento
        else if (confirmationParams.isEmpty()) {
            userInterface.showException("Parâmetros para uma confirmação automática não foram encontrados", false);
            return PWRet.CANCEL;
        }

        //Envia para a biblioteca de pagamento a confirmação com os valores informados (automaticamente ou manual).
        PWRet pwRet = LibFunctions.confirmTransaction(confirmationType, new LinkedList<>(PGWeb.confirmData.values()));
        if(pwRet != PWRet.OK)
                 return pwRet;

        /*
         * Conforme a arquitetura utilizada pela automação, esse ponto poderá estar rodando em uma thread.
         * Portanto o tratamento abaixo é feito para que a thread não seja interrompida até que a confirmação
         * seja enviada ao servidor.
         * 
         * Para versões de biblioteca iguais ou superiores a 4.0.96.0, poderá ser utilizada a função:
         * PW_iWaitConfirmation
         * */
        PWRet iRet = null;
        byte[] displayMessage = new byte[100];

		for (;;) {
			iRet = LibFunctions.eventLoop(displayMessage);
			if (iRet != PWRet.NOTHING)
				break;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/* Fim do tratamento */
        return pwRet;

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
            selectedOption = userInterface.requestSelection("Selecione o tipo de confirmação", confirmationTypeOptions);

            if (selectedOption == -1) {
                return null;
            }
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
            selectedOption = userInterface.requestSelection("Ação", actions);
        } while(selectedOption < 0 || selectedOption > 1);

        return selectedOption;
    }

    /**
     * Solicita ao usuários os parâmetros de uma transação pendente.
     */
    private void retrieveConfirmationData() {
        PGWeb.confirmData.clear();

        for (PWInfo info : requiredPendingConfirmationParams) {
            String response = userInterface.requestParam("Parâmetros da transação pendente", info.toString(), "");
            confirmationParams.put(info, response);
        }
    }

    /**
     * Busca na PGW os parâmetros de uma transação pendente.
     */
    static LinkedHashMap<PWInfo, String> getConfirmationData(UserInterface userInterface, boolean pendingTransaction) {
        LinkedHashMap<PWInfo, String> params = new LinkedHashMap<>();
        byte[] value;

        userInterface.logInfo("Buscando automaticamente os parâmetros de confirmação da transação.");

        List<PWInfo> pendingParams = pendingTransaction ? requiredPendingConfirmationParams : requiredConfirmationParams;

        for (PWInfo info : pendingParams) {
            value = new byte[1000];

            LibFunctions.getResult(info, value);
            userInterface.logInfo(info + "<0X" + Integer.toHexString(info.getValue()) + "> = " + new String(value));

            params.put(info, new String(value));
        }

        return params;
    }
}
