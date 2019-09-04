package br.com.paygo.interop;

import br.com.paygo.enums.PWData;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import br.com.paygo.exception.MandatoryParamException;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.helper.TypedDataHandler;
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

    public PWRet start() throws Exception {
        PWRet ret = LibFunctions.newTransaction(this.operation);

        if (ret == PWRet.OK) {
            this.addMandatoryParams();
        }

        return ret;
    }

    public PWRet getResult(PWInfo param) throws InvalidReturnTypeException {
        this.value = new byte[1000];
        return LibFunctions.getResult(param);
    }

    public PWRet executeTransaction() throws InvalidReturnTypeException, InterruptedException {
        this.numParams.setValue((short) 10);
        Thread.sleep(500);
        return LibFunctions.executeTransaction(getData, numParams);
    }

    public String getValue(boolean formatted) {
        return formatted ? TextFormatter.formatByteMessage(this.value) : new String(this.value);
    }

    public void retrieveMoreData() throws InvalidReturnTypeException {
        Scanner scan = new Scanner(System.in);

        System.out.println("Param size: " + this.numParams.getValue());

        for (int index = 0; index < this.numParams.getValue(); index++) {
            PWGetData pwGetData = this.getData[index];

            System.out.println("\n==== PARAM GETDATA ====");
            System.out.println("getTipoDeDado(): " + pwGetData.getTipoDeDado());
            System.out.println("getTiposEntradaPermitidos(): " + pwGetData.getTipoEntradaPermitido());
            System.out.println("getTamanhoMinimo(): " + pwGetData.getTamanhoMinimo());
            System.out.println("getTamanhoMaximo(): " + pwGetData.getTamanhoMaximo());
            System.out.println("getMascaraDeCaptura(): " + pwGetData.getMascaraDeCaptura());
            System.out.println("getMsgPrevia(): " + pwGetData.getMsgPrevia());
            System.out.println("getValorInicial(): " + pwGetData.getValorInicial());
            System.out.println("==== ==== ====\n");

            if (pwGetData.getTipoDeDado() == PWData.USERAUTH) {
                System.out.println("DIGITE A SENHA:");
                System.out.println("=> PW_iAddParam: " + this.addParam(pwGetData.getIdentificador(), scan.nextLine()));
            } else if (pwGetData.getTipoDeDado() == PWData.TYPED) {
                String typedData = TypedDataHandler.getTypedData(pwGetData.getPrompt(), pwGetData.getTamanhoMaximo(),
                        pwGetData.getTamanhoMinimo(), pwGetData.getTipoEntradaPermitido(), pwGetData.getValorInicial());

                System.out.println("=> PW_iAddParam: " + this.addParam(pwGetData.getIdentificador(), typedData));
            } else if(pwGetData.getTipoDeDado() == PWData.PPREMCRD) {
                System.out.println("Saindo do fluxo pelo RemoveCard: " + pwGetData.getPrompt());
                System.out.println("=> PW_iPPRemoveCard: " + LibFunctions.removeCardFromPINPad());

                PWRet eventLoopResponse = EventLoop.execute(this.displayMessage);

                if (eventLoopResponse == PWRet.CANCEL) {
                    if (this.abort() == PWRet.OK) {
                        System.out.println("--------- OPERAÇÃO CANCELADA NO PIN-PAD ---------");
                    }
                }
            }
        }


        System.out.println("\niGetResult(STATUS): " + this.getResult(PWInfo.STATUS) +
                "\nValue(STATUS): " + this.getValue(true));
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

    private PWRet abort() throws InvalidReturnTypeException {
        return LibFunctions.abortTransaction();
    }

    private void addMandatoryParams() throws InvalidReturnTypeException, MandatoryParamException {
        for (Map.Entry<PWInfo, String> entry : mandatoryParams.entrySet()) {
            PWRet ret = addParam(entry.getKey(), entry.getValue());

            if (ret != PWRet.OK) {
                throw new MandatoryParamException("Parâmetro obrigatório {" + entry.getKey() + ", " + entry.getValue() + "} inválido.");
            }
        }
    }

    private PWRet addParam(PWInfo param, String data) throws InvalidReturnTypeException {
        return LibFunctions.addParam(param, data);
    }
}