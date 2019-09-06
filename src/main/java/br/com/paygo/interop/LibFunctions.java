package br.com.paygo.interop;

import br.com.paygo.enums.PWCnf;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import com.sun.jna.ptr.ShortByReference;

import java.util.List;

public class LibFunctions {
    static private PGWebLib pgWebLib = new PGWebLib();

    public static PWRet init(String path) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.init(path));
    }

    static PWRet newTransaction(PWOper operation) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.newTransaction(operation.getValue()));
    }

    static PWRet addParam(PWInfo param, String paramValue) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.addParam(param.getValue(), paramValue));
    }

    static PWRet getResult(PWInfo param, byte[] paramValue) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.getResult(param, paramValue));
    }

    static PWRet executeTransaction(PWGetData[] getData, ShortByReference numParams) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.executeTransaction(getData, numParams));
    }

    static PWRet confirmTransaction(PWCnf confirmationType, List<String> params) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.confirmTransaction(confirmationType, params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
    }

    static PWRet removeCardFromPINPad() throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.PINPadRemoveCard());
    }

    static PWRet eventLoop(byte[] displayMessage) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.PINPadEventLoop(displayMessage, displayMessage.length));
    }

    static PWRet abortTransaction() throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.PINPadAbort());
    }

    static PWRet getCardFromPINPad(short index) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.PINPadGetCard(index));
    }

    static PWRet offlineCardProcessing(short index) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.PINPadChipCardOfflineProcessing(index));
    }

    static PWRet finishOfflineProcessing(short index) throws InvalidReturnTypeException {
        return PWRet.valueOf(pgWebLib.PINPadChipCardFinishOfflineProcessing(index));
    }
}
