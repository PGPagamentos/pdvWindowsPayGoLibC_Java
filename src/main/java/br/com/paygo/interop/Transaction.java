package br.com.paygo.interop;

import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.enums.PWValidDataEntry;
import br.com.paygo.exception.InvalidReturnTypeException;
import br.com.paygo.exception.MandatoryParamException;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.helper.UserInputHandler;
import br.com.paygo.ui.UserInterface;
import com.sun.jna.ptr.ShortByReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Transaction {

    private static final Map<PWInfo, String> mandatoryParams = new HashMap<PWInfo, String>() {{
        put(PWInfo.AUTDEV, "SETIS AUTOMACAO E SISTEMA LTDA");
        put(PWInfo.AUTVER, "1.1.0.0");
        put(PWInfo.AUTNAME, "PGWEBLIBTEST");
        put(PWInfo.AUTCAP, "15");
    }};

    private static final Map<PWInfo, String> saleParams = new HashMap<PWInfo, String>() {{
        put(PWInfo.CURRENCY, "986");
        put(PWInfo.CURREXP, "2");
    }};

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

    public PWRet start() throws Exception {
        PWRet ret = LibFunctions.newTransaction(this.operation);

        if (ret == PWRet.OK) {
            userInterface.logInfo("=> PW_iNewTransac: " + ret.toString());
            this.addMandatoryParams();

            externalParams = userInterface.getParams();
        }

        return ret;
    }

    public PWRet executeTransaction() throws InvalidReturnTypeException {
        return LibFunctions.executeTransaction(getData, numParams);
    }

    public PWRet getResult(PWInfo param) throws InvalidReturnTypeException {
        return LibFunctions.getResult(param, value);
    }

    public void retrieveMoreData() throws InvalidReturnTypeException {
        System.out.println("\nParam size: " + this.numParams.getValue());

        for (short index = 0; index < this.numParams.getValue(); index++) {
            PWGetData pwGetData = this.getData[index];
            PWInfo identifier = pwGetData.getIdentificador();

            printGetData(index);

            if (identifier != PWInfo.NONE && externalParams.containsKey(identifier)) {
                String paramValue = externalParams.get(identifier);
                externalParams.remove(identifier);

                this.addParam(identifier, paramValue);
            } else {
                switch (pwGetData.getTipoDeDado()) {
                    case MENU:
                        Menu menu = new Menu(pwGetData);
                        String optionSelected = UserInputHandler.requestSelectionFromMenu(userInterface, menu);

                        this.addParam(identifier, optionSelected);
                        break;
                    case USERAUTH:
                        String password = UserInputHandler.getTypedData(userInterface, "Digite a senha:",
                                20, 4, PWValidDataEntry.ALPHANUMERIC);
                        this.addParam(identifier, password);
                        break;
                    case TYPED:
                        String typedData = UserInputHandler.getTypedData(userInterface, pwGetData.getPrompt(),
                                pwGetData.getTamanhoMaximo(), pwGetData.getTamanhoMinimo(),
                                pwGetData.getTipoEntradaPermitido(), pwGetData.getValorInicial(),
                                pwGetData.getMascaraDeCaptura());

                        this.addParam(identifier, typedData);
                        break;
                    case PPREMCRD:
                        System.out.println("Saindo do fluxo pelo RemoveCard: " + pwGetData.getPrompt());
                        userInterface.logInfo("=> PW_iPPRemoveCard: " + LibFunctions.removeCardFromPINPad());

                        executeEventLoop();
                        break;
                    case CARDINF:
                        userInterface.logInfo(pwGetData.getPrompt());
                        if (pwGetData.getTipoEntradaCartao() == 1) { // digitado
                            System.out.println("Digite o numero do cartão");

                            String cardNumber = UserInputHandler.getTypedData(userInterface,
                                    "Digite o numero do cartão:", 20,
                                    16, PWValidDataEntry.NUMERIC);

                            this.addParam(PWInfo.CARDFULLPAN, cardNumber);
                            userInterface.logInfo("=> PW_iGetResult: " + this.getResult(PWInfo.CARDFULLPAN));
                        } else { // pin-pad
                            userInterface.logInfo("CAPTURA DE DADOS DO PIN-PAD");
                            userInterface.logInfo("=> PW_iPPGetCard: " + LibFunctions.getCardFromPINPad(index));
                            executeEventLoop();
                        }
                        break;
                    case CARDOFF:
                        userInterface.logInfo("=> PW_iPPGoOnChip: " + LibFunctions.offlineCardProcessing(index));
                        executeEventLoop();
                        break;
                    case CARDONL:
                        userInterface.logInfo("=> PW_iPPFinishChip: " + LibFunctions.finishOfflineProcessing(index));
                        executeEventLoop();
                        break;
                }
            }
        }

        userInterface.logInfo("\n=> PW_iGetResult: " + this.getResult(PWInfo.STATUS) +
                "\nSTATUS: " + this.getValue(true));
    }

    public PWRet finalizeTransaction() {
        PWRet ret = PWRet.OK;

        try {
            this.value = new byte[1000];
            getResult(PWInfo.CNFREQ);
            System.out.println("Transação deve ser confirmada: " + new String(this.value));

            if (new String(this.value).trim().equals("1")) {
                HashMap<PWInfo, String> confirmationParams = this.getConfirmationParams();

                Confirmation confirmation = new Confirmation(this, confirmationParams);

                ret = confirmation.executeConfirmationProcess(false);
                userInterface.logInfo("=> PW_iConfirmation: " + ret);
            }
        } catch (InvalidReturnTypeException e) {
            System.out.println("Erro ao confirmar a transação!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public PWRet abort() throws InvalidReturnTypeException {
        return LibFunctions.abortTransaction();
    }

    public String getValue(boolean formatted) {
        return formatted ? TextFormatter.formatByteMessage(this.value) : new String(this.value);
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    private void executeEventLoop() throws InvalidReturnTypeException {
        PWRet eventLoopResponse = EventLoop.execute(userInterface, this.displayMessage);

        if (eventLoopResponse == PWRet.CANCEL) {
            if (this.abort() == PWRet.OK) {
                System.out.println("--------- OPERAÇÃO CANCELADA ---------");
            }
        }
    }

    private void addMandatoryParams() throws MandatoryParamException {
        for (Map.Entry<PWInfo, String> entry : mandatoryParams.entrySet()) {
            PWRet ret = addParam(entry.getKey(), entry.getValue());

            if (ret != PWRet.OK) {
                throw new MandatoryParamException("Parâmetro obrigatório {" + entry.getKey() + ", " + entry.getValue() + "} inválido.");
            }
        }

        if (this.operation == PWOper.SALE) {
            for (Map.Entry<PWInfo, String> entry : saleParams.entrySet()) {
                PWRet ret = addParam(entry.getKey(), entry.getValue());

                if (ret != PWRet.OK) {
                    throw new MandatoryParamException("Parâmetro obrigatório {" + entry.getKey() + ", " + entry.getValue() + "} inválido.");
                }
            }
        }
    }

    private PWRet addParam(PWInfo param, String data) {
        try {
            PWRet code = LibFunctions.addParam(param, data);
            userInterface.logInfo("Parâmetro '" + param + "(" + param.getValue() + "): " + data + "' adicionado.");

            return code;
        } catch (InvalidReturnTypeException e) {
            userInterface.showException(e.getMessage(), false);
        }

        return PWRet.INVPARAM;
    }

    private HashMap<PWInfo, String> getConfirmationParams() throws InvalidReturnTypeException {
        HashMap<PWInfo, String> confirmationParams = new HashMap<>();

        for (PWInfo info : Arrays.asList(PWInfo.REQNUM, PWInfo.AUTLOCREF, PWInfo.AUTEXTREF, PWInfo.VIRTMERCH, PWInfo.AUTHSYST)) {
            getResult(info);
            confirmationParams.put(info, new String(value));
        }

        return confirmationParams;
    }

    private void printGetData(int index) {
        PWGetData pwGetData = this.getData[index];

        System.out.println("\n==== PARAMS GETDATA ====");

        try {
            System.out.println("IDENTIFICADOR: " + pwGetData.getIdentificador());
            System.out.println("TIPO DE DADO: " + pwGetData.getTipoDeDado());
            System.out.println("TIPO ENTRADA PERMITIDA: " + pwGetData.getTipoEntradaPermitido());
            System.out.println("TAMANHO MINIMO: " + pwGetData.getTamanhoMinimo());
            System.out.println("TAMANHO MAXIMO: " + pwGetData.getTamanhoMaximo());
            System.out.println("MASCARA DE CAPTURA: " + pwGetData.getMascaraDeCaptura());
            System.out.println("MSG PREVIA: " + pwGetData.getMsgPrevia());
            System.out.println("VALOR INICIAL: " + pwGetData.getValorInicial());
        } catch (InvalidReturnTypeException e) {
            System.out.println("-- ERRO AO EXIBIR OS DADOS --");
            System.out.println(pwGetData);
        }

        System.out.println("==== ==== ====\n");
    }
}