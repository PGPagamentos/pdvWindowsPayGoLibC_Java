package br.com.paygo;

import br.com.paygo.enums.PWCnf;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWUserDataMessage;
import br.com.paygo.model.PWGetData;
import br.com.paygo.model.PWParameter;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.ptr.ShortByReference;

class PGWebLib {

    private static final String libName = Platform.isLinux() ? "PGWebLib.so" : "PGWebLib.dll";
    private final ShortByReference numParams = new ShortByReference((short)10);
    private final PGWebLibMap libInterface;
    private PWGetData[] getData;
    private PWParameter[] pwParameters;

    static {
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.debug_load.jna", "true");
        System.setProperty("jna.platform.library.path", "/usr/local");
    }

    PGWebLib() {
        this.libInterface = Native.load(libName, PGWebLibMap.class);
        getData = (PWGetData[]) new PWGetData().toArray(numParams.getValue());
    }

    short init(String path) {
        return libInterface.PW_iInit(path);
    }

    short newTransaction(PWOper operation) {
        return libInterface.PW_iNewTransac(operation.getValue());
    }

    short addParam(PWInfo param, String paramValue) {
        return libInterface.PW_iAddParam(param.getValue(), paramValue);
    }

    short executeTransaction() {
        return libInterface.PW_iExecTransac(getData, numParams);
    }

    short getResult(PWInfo param) {
        byte[] paramValue = new byte[1000];

        return libInterface.PW_iGetResult(param.getValue(), paramValue, paramValue.length);
    }

    short confirmTransaction(PWCnf transactionResult, String transactionLocalRef, String transactionPGWRef,
                             String transactionProviderRef, String storeId, String providerName) {

        return libInterface.PW_iConfirmation(transactionResult.getValue(), transactionLocalRef, transactionPGWRef,
                transactionProviderRef, storeId, providerName);
    }

    short idleProd() {
        return libInterface.PW_iIdleProc();
    }

    short PINPadEventLoop(byte[] displayMessage, int displaySize) {
        return libInterface.PW_iPPEventLoop(displayMessage, displaySize);
    }

    short PINPadAbort() {
        return libInterface.PW_iPPAbort();
    }

    short PINPadGetCard(short index) {
        return libInterface.PW_iPPGetCard(index);
    }

    short PINPadRemoveCard() {
        return libInterface.PW_iPPRemoveCard();
    }

    short PINPadGetPIN(short index) {
        return libInterface.PW_iPPGetPIN(index);
    }

    short PINPadGetData(short index) {
        return libInterface.PW_iPPGetData(index);
    }

    short PINPadGetUserData(PWUserDataMessage userDataMessage, byte minLength, byte maxLength, short timeout, String value) {
        return libInterface.PW_iPPGetUserData((short)userDataMessage.ordinal(), minLength, maxLength, timeout, value);
    }

    short PINPadChipCardOfflineProcessing(short index) {
        return libInterface.PW_iPPGoOnChip(index);
    }

    short PINPadChipCardFinishOfflineProcessing(short index) {
        return libInterface.PW_iPPFinishChip(index);
    }

    short PINPadRequestConfirmation(short index) {
        return libInterface.PW_iPPConfirmData(index);
    }

    short PINPadConfirmData(short index) {
        return libInterface.PW_iPPDataConfirmation(index);
    }

    short PINPadGenericCommand(short index) {
        return libInterface.PW_iPPGenericCMD(index);
    }

    short PINPadShowMessage(String message) {
        return libInterface.PW_iPPDisplay(message);
    }

//    short PINPadWaitForEvent() {
//        return libInterface.PW_iPPWaitEvent();
//    }
}
