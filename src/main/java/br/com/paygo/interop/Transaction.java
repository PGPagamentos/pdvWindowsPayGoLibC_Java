package br.com.paygo.interop;

import br.com.paygo.enums.PWData;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.helper.TypedDataHandler;
import com.sun.jna.ptr.ShortByReference;

import java.util.Scanner;

public class Transaction {
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
        return LibFunctions.newTransaction(this.operation);
    }

    public PWRet addParam(PWInfo param, String data) throws Exception {
        return LibFunctions.addParam(param, data);
    }

    public PWRet addMandatoryParams() throws Exception {
        PWRet PWRet = addParam(PWInfo.AUTDEV, "SETIS AUTOMACAO E SISTEMA LTDA");

        if (PWRet != PWRet.OK)
            return PWRet;

        PWRet = addParam(PWInfo.AUTVER, "1.1.0.0");
        if (PWRet != PWRet.OK)
            return PWRet;

        PWRet = addParam(PWInfo.AUTNAME, "PGWEBLIBTEST");
        if (PWRet != PWRet.OK)
            return PWRet;

        PWRet = addParam(PWInfo.AUTCAP, "15");
        return PWRet;
    }

    public PWRet getResult(PWInfo param) throws Exception {
        return LibFunctions.getResult(param);
    }

    public PWRet executeTransaction() throws InterruptedException, Exception {
        Thread.sleep(500);
        return LibFunctions.executeTransaction(getData, numParams);
    }

    public void showTransactionStatus() throws Exception {
        if(this.numParams.getValue() != 10) {
            System.out.println("Dados da Transação\n=========================================================\n");
        }

        for (PWInfo param : PWInfo.values()){
            PWRet returnedCode = this.getResult(param);

            if (returnedCode == PWRet.OK) {
                boolean formatOutput = !(param == PWInfo.RCPTMERCH || param == PWInfo.RCPTCHOLDER     //Se não for um recibo formata a msg
                        || param == PWInfo.RCPTCHSHORT || param == PWInfo.RCPTFULL);

                System.out.println(param + ": " + getValue(formatOutput));
            }
        }
    }

    public String getValue(boolean formatted) {
        return formatted ? TextFormatter.formatByteMessage(this.value) : new String(this.value);
    }

    public void retrieveMoreData() throws Exception {
        Scanner scan = new Scanner(System.in);

        System.out.println("Param size: " + this.numParams.getValue());

        for (int index = 0; index <= this.numParams.getValue() - 1; index++) {
            PWGetData pwGetData = this.getData[0];

            System.out.println("this.getData[index] = " + pwGetData);

            System.out.println("\n==== PARAM GETDATA ====");
            System.out.println("getTipoDeDado(): " + pwGetData.getTipoDeDado());
            System.out.println("getTiposEntradaPermitidos(): " + pwGetData.getTiposEntradaPermitidos());
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
                        pwGetData.getTamanhoMinimo(), pwGetData.tiposEntradaPermitidos, pwGetData.getValorInicial());

                System.out.println("=> PW_iAddParam: " + this.addParam(pwGetData.getIdentificador(), typedData));
            }
        }
    }
}