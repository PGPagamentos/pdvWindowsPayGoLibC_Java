package br.com.paygo.interop;

import br.com.paygo.enums.*;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.ShortByReference;

import java.util.LinkedList;

public class LibFunctions {
    static private PGWebLib pgWebLib = new PGWebLib();

    public static PWRet init(String path) {
        return PWRet.valueOf(pgWebLib.init(path));
    }

    static PWRet newTransaction(PWOper operation) {
        return PWRet.valueOf(pgWebLib.newTransaction(operation.getValue()));
    }

    static PWRet addParam(PWInfo param, String paramValue) {
        return PWRet.valueOf(pgWebLib.addParam(param.getValue(), paramValue));
    }

    public static PWRet getResult(PWInfo param, byte[] paramValue) {
        return PWRet.valueOf(pgWebLib.getResult(param, paramValue));
    }

    static PWRet executeTransaction(PWGetData[] getData, ShortByReference numParams) {
        return PWRet.valueOf(pgWebLib.executeTransaction(getData, numParams));
    }

    static PWRet confirmTransaction(PWCnf confirmationType, LinkedList<String> params) {
        return PWRet.valueOf(pgWebLib.confirmTransaction(confirmationType, params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
    }

    static PWRet removeCardFromPINPad() {
        return PWRet.valueOf(pgWebLib.PINPadRemoveCard());
    }

    static PWRet eventLoop(byte[] displayMessage) {
        return PWRet.valueOf(pgWebLib.PINPadEventLoop(displayMessage, displayMessage.length));
    }

    static PWRet abortTransaction() {
        return PWRet.valueOf(pgWebLib.PINPadAbort());
    }

    static PWRet getCardFromPINPad(short index) {
        return PWRet.valueOf(pgWebLib.PINPadGetCard(index));
    }

    static PWRet offlineCardProcessing(short index) {
        return PWRet.valueOf(pgWebLib.PINPadChipCardOfflineProcessing(index));
    }

    static PWRet finishOfflineProcessing(short index) {
        return PWRet.valueOf(pgWebLib.PINPadChipCardFinishOfflineProcessing(index));
    }

    static PWRet getPIN(short index) {
        return PWRet.valueOf(pgWebLib.PINPadGetPIN(index));
    }

    public static PWRet getPINBlock(String key, int minLength, int maxLength, int seconds, String promptMessage, byte[] data) {
        return PWRet.valueOf(pgWebLib.PINPadGetPINBlock(key, minLength, maxLength, seconds, promptMessage, data));
    }

    public static PWRet getUserDataOnPINPad(PWUserDataMessage userDataMessage, int minSize, int maxSize, int timeout, byte[] value) {
        return PWRet.valueOf(pgWebLib.PINPadGetUserData(userDataMessage, (byte)minSize, (byte)maxSize, (short)timeout, value));
    }

    static PWRet showMessageOnPINPad(String message) {
        return PWRet.valueOf(pgWebLib.PINPadShowMessage(message));
    }

    static PWRet waitEventOnPINPad(LongByReference event) {
        return PWRet.valueOf(pgWebLib.PINPadWaitEvent(event));
    }

}
