package br.com.paygo.interop;

import br.com.paygo.enums.*;
import br.com.paygo.exceptions.InvalidDataType;
import br.com.paygo.exceptions.MandatoryParamException;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.helper.UserInputHandler;
import br.com.paygo.ui.UserInterface;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.ShortByReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe responsável por executar todas as etapas de uma transação através da biblioteca PGWebLib
 */
public class Transaction {

    private static final Map<PWInfo, String> mandatoryParams = new HashMap<PWInfo, String>() {{
        put(PWInfo.AUTDEV, "AUTOMACAO DE SISTEMAS");
        put(PWInfo.AUTVER, ApplicationProperties.INSTANCE.getAppVersion());
        put(PWInfo.AUTNAME, ApplicationProperties.INSTANCE.getAppName());
        put(PWInfo.AUTCAP, "28");
        put(PWInfo.AUTHTECHUSER, "PAYGOTESTE");
    }};

    private boolean selfService = false;
    private final UserInterface userInterface;
    private final ShortByReference numParams;
    private PWOper operation;
    private PWGetData[] getData;
    private HashMap<PWInfo, String> externalParams;
    private byte[] displayMessage;
    private byte[] value;

    public Transaction(PWOper operation, UserInterface userInterface) {
        this.operation = operation;
        this.userInterface = userInterface;
        this.numParams = new ShortByReference((short)10);
        this.getData = (PWGetData[]) new PWGetData().toArray(numParams.getValue());
        this.externalParams = new HashMap<>();
        this.displayMessage = new byte[128];
        this.value = new byte[1000];
    }

    /**
     * Método responsável por executar o fluxo completo de uma transação (Inicialização, envio de parâmetros e finalização/confirmação)
     */
    public PWRet executeOperation() {
        PWRet returnedCode;
        boolean abort = false;

        try {
            // inicia a transação
            returnedCode = this.start();

            if (returnedCode == PWRet.OK) {
                do {
                    returnedCode = this.executeTransaction();
                    userInterface.logInfo("=> PW_iExecTransac: " + returnedCode);

                    // verifica se precisam ser informados mais parâmetros para a transação
                    if (returnedCode == PWRet.MOREDATA) {
                        if (this.retrieveMoreData() == PWRet.CANCEL) {
                            abort = true;
                        }
                    }
                } while (returnedCode == PWRet.MOREDATA && !abort);

                if (abort) {
                    return PWRet.CANCEL;
                } else if (returnedCode == PWRet.OK) {
                    if (this.operation == PWOper.SALE || this.operation == PWOper.REPRINT) {
                        this.printReceipt();
                    }

                    this.finalizeTransaction();

                    this.getResult(PWInfo.RESULTMSG);
                    userInterface.logInfo("\n\tRESPOSTA: " + getValue(true));
                } else {
                    handleUnexpectedReturnCode(returnedCode);
                }
            } else {
                this.getResult(PWInfo.RESULTMSG);
                userInterface.logInfo("\n\tRESPOSTA: " + getValue(true));
            }

            return returnedCode;
        } catch (Exception e) {
            userInterface.showException(e.getMessage(), true);
            return PWRet.INTERNALERR;
        }
    }

    public PWRet initInteractionOnPINPad() throws Exception {
        PINPad pinPad = PINPad.getInstance();
        LongByReference event = new LongByReference(0);
        int eventResponse = 0;
        PWRet ret;

        ret = pinPad.displayMessage("INSIRA OU PASSE O CARTAO");

        if (ret != PWRet.OK) {
            userInterface.showException("Erro ao exibir mensagem no PIN-pad.", false);
            return PWRet.PINPADERR;
        }

        do {
            ret = LibFunctions.eventLoop(this.displayMessage);

            if (ret == PWRet.TIMEOUT) {
                userInterface.showException("Timeout!", false);
                return PWRet.TIMEOUT;
            }
        } while (ret != PWRet.OK);

        ret = LibFunctions.waitEventOnPINPad(event);

        if (ret != PWRet.OK) {
            userInterface.showException("Erro no wait event do PIN_pad", false);
            return PWRet.PINPADERR;
        }

        do {
            Thread.sleep(500);

            ret = LibFunctions.eventLoop(this.displayMessage);

            if (ret == PWRet.TIMEOUT) {
                return PWRet.TIMEOUT;
            }

            if (ret == PWRet.OK) {
                eventResponse = (int)event.getValue();

                if(eventResponse == PWPINPadInput.KEYCANC.getValue()) {
                    this.abort(true);
                    return PWRet.CANCEL;
                }
            }
        } while (!Arrays.asList(PWPINPadInput.MAGSTRIPE.getValue(), PWPINPadInput.ICC.getValue(), PWPINPadInput.CTLS.getValue()).contains(eventResponse));

        ret = pinPad.displayMessage("PROCESSANDO...");

        if (ret != PWRet.OK) {
            userInterface.showException("Erro ao exibir mensagem no PIN-pad.", false);
            return PWRet.PINPADERR;
        }

        this.selfService = true;

        return PWRet.OK;
    }

    private void getResult(PWInfo param) {
        LibFunctions.getResult(param, value);
    }

    /**
     * Método responsável por cancelar uma transação em execução
     */
    public void abort(boolean isPINPad) {
        if (isPINPad) {
            PWRet ret = LibFunctions.abortTransaction();
            userInterface.logInfo("=> PW_iPPAbort: " + ret.toString());

            LibFunctions.showMessageOnPINPad("OPERACAO\nCANCELADA");
        }

        userInterface.showException("Transação cancelada", false);
        userInterface.logInfo("\n\t--------- OPERAÇÃO CANCELADA ---------\n");
    }

    private void printReceipt() {
        userInterface.logInfo("\n\t------ VIA ESTABELECIMENTO ------");
        getResult(PWInfo.RCPTMERCH);
        userInterface.logInfo(this.getValue(false));

        userInterface.logInfo("\n\t------ VIA CLIENTE ------");
        getResult(PWInfo.RCPTCHOLDER);
        userInterface.logInfo(this.getValue(false));
    }

    public String getValue(boolean formatted) {
        return formatted ? TextFormatter.formatByteMessage(this.value) : new String(this.value);
    }

    /**
     * Busca todas as informações de resultados disponíveis no momento
     */
    public void printResultParams() {
        PWRet ret;
        byte[] tempValue;

        userInterface.logInfo("============== PARAMETROS ==============");

        // é feito uma varredura de todos os valores de 0 a Short.MAX_VALUE
        // poderia ser feita uma iteração sob os valores de PWInfo, mas nem todos os parâmetros possíveis estão mapeados
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            tempValue = new byte[1000];

            ret = LibFunctions.getResult(i, tempValue);

            if (ret == PWRet.OK) {
                userInterface.logInfo(PWInfo.valueOf((short)i) + "<0X" + Integer.toHexString(i) + "> = " + TextFormatter.formatByteMessage(tempValue));
            }
        }
        userInterface.logInfo("========================================\n");
    }

    UserInterface getUserInterface() {
        return userInterface;
    }

    private PWRet start() throws Exception {
        PWRet ret = LibFunctions.newTransaction(this.operation);

        if (ret == PWRet.OK) {
            userInterface.logInfo("=> PW_iNewTransac: " + ret.toString());
            this.addMandatoryParams();

            externalParams = userInterface.getParams();
        }

        return ret;
    }

    /**
     * Método responsável por enviar para a biblioteca PGWebLib os parâmetros obrigatórios para uma transação
     */
    private void addMandatoryParams() throws MandatoryParamException {
        userInterface.logInfo("\nAdicionando parâmetros obrigatórios: ");

        for (Map.Entry<PWInfo, String> entry : mandatoryParams.entrySet()) {
            PWRet ret = addParam(entry.getKey(), entry.getValue());

            if (ret != PWRet.OK) {
                throw new MandatoryParamException("Parâmetro obrigatório {" + entry.getKey() + ", " + entry.getValue() + "} inválido.");
            }
        }

        userInterface.logInfo("");
    }

    private PWRet addParam(PWInfo param, String data) {
        PWRet code = LibFunctions.addParam(param, data);
        userInterface.logInfo("=> PW_iAddParam (" + param.getValue() + " - " + param + "): " + data);

        return code;
    }

    private PWRet executeTransaction() {
        return LibFunctions.executeTransaction(getData, numParams);
    }

    /**
     * Método responsável por solicitar e enviar parâmetros para a biblioteca PGWebLib
     */
    private PWRet retrieveMoreData() throws InvalidDataType {
        PWRet response = PWRet.OK;
        boolean abortPINPad = false;

        for (short index = 0; index < this.numParams.getValue(); index++) {
            PWGetData pwGetData = this.getData[index];
            PWInfo identifier = pwGetData.getIdentificador();

            userInterface.logInfo("Dado requisitado = " + identifier + " | Tipo de Dado = " + pwGetData.getTipoDeDado());

            if (identifier != PWInfo.NONE && externalParams.containsKey(identifier)) {
                String paramValue = externalParams.get(identifier);
                externalParams.remove(identifier);

                this.addParam(identifier, paramValue);
            } else {
                PWRet eventLoopResponse = PWRet.OK;

                switch (pwGetData.getTipoDeDado()) {
                    case MENU:
                        String optionSelected;

                        if (this.selfService) {
                            if (pwGetData.getNumOpcoesMenu() > 3) {
                                userInterface.showException("Tamanho do menu é muito grande", false);
                                response = PWRet.CANCEL;
                                abortPINPad = true;
                            } else {
                                PINPad pinPad = PINPad.getInstance();
                                try {
                                    optionSelected = pinPad.getMenuSelection(pwGetData.getMenu(), pwGetData.getNumOpcoesMenu(), this.displayMessage);

                                    if (optionSelected.equals("-1")) {
                                        response = PWRet.CANCEL;
                                        abortPINPad = true;
                                    } else {
                                        this.addParam(identifier, optionSelected);
                                    }
                                } catch (Exception e) {
                                    userInterface.showException(e.getMessage(), false);
                                    response = PWRet.CANCEL;
                                }
                            }
                        } else {
                            Menu menu = new Menu(pwGetData);
                            optionSelected = UserInputHandler.requestSelectionFromMenu(userInterface, menu);

                            if (optionSelected.equals("-1")) {
                                response = PWRet.CANCEL;
                            } else {
                                this.addParam(identifier, optionSelected);
                            }
                        }
                        break;
                    case USERAUTH:
                        String password = UserInputHandler.getTypedData(userInterface, "Digite a senha:",
                                20, 4, PWValidDataEntry.ALPHANUMERIC);
                        this.addParam(identifier, password);
                        break;
                    case TYPED:
                        String typedData;

                        if (identifier == PWInfo.TOTAMNT && selfService) {
                            typedData = "100";
                        } else {
                            typedData = UserInputHandler.getTypedData(userInterface, pwGetData.getPrompt(),
                                    pwGetData.getTamanhoMaximo(), pwGetData.getTamanhoMinimo(),
                                    pwGetData.getTipoEntradaPermitido(), pwGetData.getValorInicial(),
                                    pwGetData.getMascaraDeCaptura());
                        }

                        if (typedData.equals("-1")) {
                           response = PWRet.CANCEL;
                        } else {
                            this.addParam(identifier, typedData);
                        }
                        break;
                    case PPREMCRD:
                        System.out.println("Saindo do fluxo pelo RemoveCard: " + pwGetData.getPrompt());
                        userInterface.logInfo("=> PW_iPPRemoveCard: " + LibFunctions.removeCardFromPINPad());

                        eventLoopResponse = executeEventLoop();
                        break;
                    case CARDINF:
                        userInterface.logInfo(pwGetData.getPrompt());
                        if (pwGetData.getTipoEntradaCartao() == 1) { // digitado
                            System.out.println("Digite o numero do cartão");

                            String cardNumber = UserInputHandler.getTypedData(userInterface,
                                    "Digite o numero do cartão:", 20,
                                    16, PWValidDataEntry.NUMERIC);

                            addParam(PWInfo.CARDFULLPAN, cardNumber);
                        } else { // pin-pad
                            userInterface.logInfo("CAPTURA DE DADOS DO PIN-PAD");
                            userInterface.logInfo("=> PW_iPPGetCard: " + LibFunctions.getCardFromPINPad(index));
                            eventLoopResponse = executeEventLoop();
                        }
                        break;
                    case CARDOFF:
                        userInterface.logInfo("=> PW_iPPGoOnChip: " + LibFunctions.offlineCardProcessing(index));
                        eventLoopResponse = executeEventLoop();
                        break;
                    case CARDONL:
                        userInterface.logInfo("=> PW_iPPFinishChip: " + LibFunctions.finishOfflineProcessing(index));
                        eventLoopResponse = executeEventLoop();
                        break;
                    case PPENCPIN:
                        userInterface.logInfo("=> PW_iPPGetPIN: " + LibFunctions.getPIN(index));
                        eventLoopResponse = executeEventLoop();
                }

                if (eventLoopResponse == PWRet.CANCEL) {
                    response = PWRet.CANCEL;
                    abortPINPad = true;
                }
            }
        }

        if (response == PWRet.CANCEL) {
            this.abort(abortPINPad);
        }

        return response;
    }

    /**
     * Método responsável por lidar com um retorno inesperado da biblioteca PGWebLib (diferente de PWRet.OK)
     */
    private void handleUnexpectedReturnCode(PWRet returnedCode) {
        switch (returnedCode) {
            case REQPARAM:
                userInterface.showException("Falha de comunicação com a infraestrutura do Pay&Go Web (falta parâmetro obrigatório).", false);
                break;
            case FROMHOSTPENDTRN:
                // relatório resumido está sendo utilizado para verificar e autorizar transações pendentes
                if (operation != PWOper.RPTTRUNC) {
                    userInterface.logInfo("===========================================\n" +
                            "== ERRO - EXITSTE UMA TRANSAÇÃO PENDENTE ==\n" +
                            "===========================================");
                } else {
                    userInterface.logInfo("\n=== CONFIRMAÇÃO TRANSAÇÃO PENDENTE ===\n");

                    Confirmation confirmation = new Confirmation(this);
                    returnedCode = confirmation.executeConfirmationProcess(true);

                    userInterface.logInfo("=> PW_iConfirmation: " + returnedCode);

                    if (returnedCode == PWRet.OK) {
                        userInterface.logInfo("\n\n=> CONFIRMAÇÃO TRANSAÇÃO PENDENTE CONCLUÍDA <=\n\n");
                    }
                }
                break;
            case PINPADERR:
                userInterface.showException("Erro de comunição com o PIN-pad", false);
                break;
            case TIMEOUT:
                userInterface.showException("Tempo limite excedido", false);
                break;
            case FROMHOST:
            default:
                this.getResult(PWInfo.RESULTMSG);
                userInterface.logInfo("\n\tRESPOSTA: " + getValue(true));
        }
    }

    private PWRet executeEventLoop() {
        return EventLoop.execute(userInterface, this.displayMessage);
    }

    /**
     * Método responsável por confirmar uma transação (quando for necessária tal confirmação)
     */
    private void finalizeTransaction() {
        try {
            this.value = new byte[1000];
            getResult(PWInfo.CNFREQ);
            userInterface.logInfo(PWInfo.CNFREQ + "<0X" + Integer.toHexString(PWInfo.CNFREQ.getValue()) + "> = " + getValue(true));

            if (new String(this.value).trim().equals("1")) {
                userInterface.logInfo("\n\tEsta transação deve ser confirmada\n");
                Confirmation confirmation = new Confirmation(this, this.getConfirmationParams());

                PWRet ret = confirmation.executeConfirmationProcess(false);
                userInterface.logInfo("=> PW_iConfirmation: " + ret);
            }
        } catch (Exception e) {
            userInterface.logInfo("\n\tErro ao confirmar a transação\n");
        }
    }

    /**
     * Método responsável por requisitar à biblioteca PGWebLib os parâmetros para confirmação de uma transação pendente.
     */
    private LinkedHashMap<PWInfo, String> getConfirmationParams() {
        LinkedHashMap<PWInfo, String> confirmationParams = new LinkedHashMap<>();

        for (PWInfo info : Arrays.asList(PWInfo.REQNUM, PWInfo.AUTLOCREF, PWInfo.AUTEXTREF, PWInfo.VIRTMERCH, PWInfo.AUTHSYST)) {
            this.value = new byte[1000];
            getResult(info);
            confirmationParams.put(info, new String(value));
        }

        return confirmationParams;
    }
}