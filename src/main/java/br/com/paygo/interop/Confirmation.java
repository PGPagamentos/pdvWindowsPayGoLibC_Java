package br.com.paygo.interop;

import br.com.paygo.enums.PWCnf;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;

import java.util.*;

/**
 * Classe responsável por realizar a confirmação de uma transação.
 * Se forem passados todos os parâmetros para o objeto, ele irá realizar a confirmação da última transação.
 * Se não forem passados os parâmetros para o objeto, ele irá realizar a confirmação de uma transação pendente.
 */
public class Confirmation {

    private Map<PWInfo, String> confirmationParams;
    private final Transaction transaction;
    private final List<PWInfo> requiredPendingParams = new ArrayList<PWInfo>() {{
        add(PWInfo.PNDREQNUM);
        add(PWInfo.PNDAUTLOCREF);
        add(PWInfo.PNDAUTEXTREF);
        add(PWInfo.PNDVIRTMERCH);
        add(PWInfo.PNDAUTHSYST);
    }};

    public Confirmation(Transaction transaction) {
        this.transaction = transaction;
        this.confirmationParams = new HashMap<>();
    }

    public Confirmation(Transaction transaction, Map<PWInfo, String> confirmationParams) {
        this.transaction = transaction;
        this.confirmationParams = confirmationParams;
    }

    public PWRet executeConfirmationProcess(boolean pendingTransaction) throws Exception {
        PWCnf confirmationType;

        if (!pendingTransaction && !confirmationParams.isEmpty()) {
            confirmationType = PWCnf.AUTO;
        } else {
            System.out.println("======== CONFIRMAÇÃO PENDENTE ========");
            Scanner scanner = new Scanner(System.in);
            int selectedOption;

            confirmationType = retrieveConfirmationType();

            do {
                System.out.println("Ação:" +
                        "\n [1] Confirmar última transação" +
                        "\n [2] Informar dados manualmente");

                selectedOption = scanner.nextInt();
            } while(selectedOption < 1 || selectedOption > 2);

            if (selectedOption == 1) {
                getConfirmationData();
            } else {
                retrieveConfirmationData();
            }
        }

        return LibFunctions.confirmTransaction(confirmationType, new ArrayList<>(confirmationParams.values()));
    }

    /**
     * Solicitação ao usuário o tipo de confirmação que deve ser realizada.
     */
    private PWCnf retrieveConfirmationType() {
        Scanner scanner = new Scanner(System.in);
        int selectedOption;

        do {
            System.out.println("Selecione o tipo de confirmação:" +
                    "\n [1] " + PWCnf.AUTO +
                    "\n [2] " + PWCnf.MANU_AUT +
                    "\n [3] " + PWCnf.REV_MANU_AUT +
                    "\n [4] " + PWCnf.REV_PRN_AUT +
                    "\n [5] " + PWCnf.REV_DISP_AUT +
                    "\n [6] " + PWCnf.REV_COMM_AUT +
                    "\n [7] " + PWCnf.REV_ABORT +
                    "\n [8] " + PWCnf.REV_OTHER_AUT +
                    "\n [9] " + PWCnf.REV_PWR_AUT +
                    "\n[10] " + PWCnf.REV_FISC_AUT);

            selectedOption = scanner.nextInt();
        } while (selectedOption < 1 || selectedOption > 10);

        return PWCnf.values()[selectedOption-1];
    }

    /**
     * Solicita ao usuários os parâmetros de uma transação pendente.
     */
    private void retrieveConfirmationData() {
        Scanner scanner = new Scanner(System.in);

        for (PWInfo info : requiredPendingParams) {
            System.out.println("-> " + info + ":");
            confirmationParams.put(info, scanner.nextLine());
        }
    }

    /**
     * Busca na PGW os parâmetros de uma transação pendente.
     */
    private void getConfirmationData() throws InvalidReturnTypeException {
        byte[] value;

        System.out.println("Buscando automaticamente os parâmetros de confirmação da transação pendente.");

        for (PWInfo info : requiredPendingParams) {
            value = new byte[1000];

            LibFunctions.getResult(info, value);

            System.out.println(info + " - " + new String(value));

            confirmationParams.put(info, new String(value));
        }
    }
}
