package br.com.paygo.interop;

import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import br.com.paygo.exception.MandatoryParamException;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.helper.UserInputHandler;
import com.sun.jna.ptr.ShortByReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

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

    private PWOper operation;
    private final ShortByReference numParams;
    private PWGetData[] getData;
    private byte[] displayMessage;
    private byte[] value;

    public Transaction(PWOper operation) {
        this.operation = operation;
        this.numParams = new ShortByReference((short)10);
        this.getData = (PWGetData[]) new PWGetData().toArray(numParams.getValue());
        this.displayMessage = new byte[128];
        this.value = new byte[1000];
    }

    public String getValue(boolean formatted) {
        return formatted ? TextFormatter.formatByteMessage(this.value) : new String(this.value);
    }

    public PWRet start() throws Exception {
        PWRet ret = LibFunctions.newTransaction(this.operation);

        if (ret == PWRet.OK) {
            this.addMandatoryParams();
        }

        return ret;
    }

    public PWRet executeTransaction() throws InvalidReturnTypeException, InterruptedException {
        Thread.sleep(500);
        return LibFunctions.executeTransaction(getData, numParams);
    }

    public PWRet getResult(PWInfo param) throws InvalidReturnTypeException {
        return LibFunctions.getResult(param, value);
    }

    public void retrieveMoreData() throws InvalidReturnTypeException {
        Scanner scan = new Scanner(System.in);

        System.out.println("Param size: " + this.numParams.getValue());

        for (short index = 0; index < this.numParams.getValue(); index++) {
            PWGetData pwGetData = this.getData[index];

            printGetData(index);

            switch (pwGetData.getTipoDeDado()) {
                case MENU:
                    Menu menu = new Menu(pwGetData);
                    String optionSelected = UserInputHandler.requestSelectionFromMenu(menu);

                    System.out.println("=> PW_iAddParam: " + this.addParam(pwGetData.getIdentificador(), optionSelected));
                    break;
                case USERAUTH:
                    System.out.println("DIGITE A SENHA:");
                    System.out.println("=> PW_iAddParam: " + this.addParam(pwGetData.getIdentificador(), scan.nextLine()));
                    break;
                case TYPED:
                    String typedData = UserInputHandler.getTypedData(pwGetData.getPrompt(), pwGetData.getTamanhoMaximo(),
                            pwGetData.getTamanhoMinimo(), pwGetData.getTipoEntradaPermitido(), pwGetData.getValorInicial());

                    System.out.println("=> PW_iAddParam: " + this.addParam(pwGetData.getIdentificador(), typedData));
                    break;
                case PPREMCRD:
                    System.out.println("Saindo do fluxo pelo RemoveCard: " + pwGetData.getPrompt());
                    System.out.println("=> PW_iPPRemoveCard: " + LibFunctions.removeCardFromPINPad());

                    executeEventLoop();
                    break;
                case CARDINF:
                    System.out.println("Prompt: " + pwGetData.getPrompt());
                    if (pwGetData.getTipoEntradaCartao() == 1) { // digitado
                        System.out.println("Digite o numero do cartão");

                        String cardNumber = scan.nextLine();

                        System.out.println("=> PW_iAddParam: " + this.addParam(PWInfo.CARDFULLPAN, cardNumber));
                        System.out.println("=> PW_iGetResult: " + this.getResult(PWInfo.CARDFULLPAN));
                    } else { // pin-pad
                        System.out.println("Aguarde a capturta no PIN PAD...");
                        System.out.println("=> PW_iPPGetCard: " + LibFunctions.getCardFromPINPad(index));
                        executeEventLoop();
                    }
                    break;
                case CARDOFF:
                    System.out.println("=> PW_iPPGoOnChip: " + LibFunctions.offlineCardProcessing(index));
                    executeEventLoop();
                    break;
                case CARDONL:
                    System.out.println("=> PW_iPPFinishChip: " + LibFunctions.finishOfflineProcessing(index));
                    executeEventLoop();
                    break;
            }
        }

        System.out.println("\niGetResult(STATUS): " + this.getResult(PWInfo.STATUS) +
                "\nValue(STATUS): " + this.getValue(true));
    }

    private void executeEventLoop() throws InvalidReturnTypeException {
        PWRet eventLoopResponse = EventLoop.execute(this.displayMessage);

        if (eventLoopResponse == PWRet.CANCEL) {
            if (this.abort() == PWRet.OK) {
                System.out.println("--------- OPERAÇÃO CANCELADA NO PIN-PAD ---------");
            }
        }
    }

    private void addMandatoryParams() throws InvalidReturnTypeException, MandatoryParamException {
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

    private PWRet addParam(PWInfo param, String data) throws InvalidReturnTypeException {
        return LibFunctions.addParam(param, data);
    }

    private PWRet abort() throws InvalidReturnTypeException {
        return LibFunctions.abortTransaction();
    }

    private void printGetData(int index) {
        PWGetData pwGetData = this.getData[index];

        System.out.println("\n==== PARAM GETDATA ====");

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

    public void showTransactionStatus() throws InvalidReturnTypeException {
        if(this.numParams.getValue() != 10) {
            System.out.println("\n============================================\n" +
                    "============ Dados da Transação ============" +
                    "\n============================================");
        }

        for (PWInfo param : PWInfo.values()) {
            PWRet returnedCode = this.getResult(param);

            if (returnedCode == PWRet.OK) {
                boolean formatOutput = Stream.of(PWInfo.RCPTMERCH, PWInfo.RCPTCHOLDER, PWInfo.RCPTCHSHORT, PWInfo.RCPTFULL).noneMatch(param::equals);

                System.out.println(param + ": " + getValue(formatOutput));
            }
        }
    }
}