package br.com.paygo.interop;

import br.com.paygo.enums.PWCnf;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWUserDataMessage;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.ptr.ShortByReference;

public class PGWebLib {

    private static final String libName = Platform.isLinux() ? "PGWebLib.so" : "PGWebLib.dll";
    private PGWebLibMap libInterface;

    static {
        System.setProperty("jna.debug_load", "true");
        System.setProperty("jna.debug_load.jna", "true");
        System.setProperty("jna.platform.library.path", "/tmp");
    }

    public PGWebLib() {
        Native.setProtected(true);
        this.libInterface = Native.load(libName, PGWebLibMap.class);
    }

    public short init(String path) {
        return libInterface.PW_iInit(path);
    }

    public short newTransaction(int operation) {
        return libInterface.PW_iNewTransac(operation);
    }

    public short addParam(int param, String paramValue) {
        return libInterface.PW_iAddParam(param, paramValue);
    }

    public short getResult(PWInfo param, byte[] paramValue) {
        return libInterface.PW_iGetResult(param.getValue(), paramValue, paramValue.length);
    }

    public short executeTransaction(PWGetData[] getData, ShortByReference numParams) {
        return libInterface.PW_iExecTransac(getData, numParams);
    }

    public short confirmTransaction(PWCnf confirmationType, String transactionLocalRef, String transactionPGWRef,
                                    String transactionProviderRef, String storeId, String providerName) {

        return libInterface.PW_iConfirmation(confirmationType.getValue(), transactionLocalRef, transactionPGWRef,
                transactionProviderRef, storeId, providerName);
    }

    public short idleProd() {
        return libInterface.PW_iIdleProc();
    }

    public short PINPadEventLoop(byte[] displayMessage, int displaySize) {
        return libInterface.PW_iPPEventLoop(displayMessage, displaySize);
    }

    public short PINPadAbort() {
        return libInterface.PW_iPPAbort();
    }

    public short PINPadGetCard(short index) {
        return libInterface.PW_iPPGetCard(index);
    }

    public short PINPadRemoveCard() {
        return libInterface.PW_iPPRemoveCard();
    }

    public short PINPadGetPIN(short index) {
        return libInterface.PW_iPPGetPIN(index);
    }

    public short PINPadGetData(short index) {
        return libInterface.PW_iPPGetData(index);
    }

    public short PINPadGetUserData(PWUserDataMessage userDataMessage, byte minLength, byte maxLength, short timeout, String value) {
        return libInterface.PW_iPPGetUserData((short)userDataMessage.ordinal(), minLength, maxLength, timeout, value);
    }

    public short PINPadChipCardOfflineProcessing(short index) {
        return libInterface.PW_iPPGoOnChip(index);
    }

    public short PINPadChipCardFinishOfflineProcessing(short index) {
        return libInterface.PW_iPPFinishChip(index);
    }

    public short PINPadRequestConfirmation(short index) {
        return libInterface.PW_iPPConfirmData(index);
    }

    public short PINPadConfirmData(short index) {
        return libInterface.PW_iPPDataConfirmation(index);
    }

    public short PINPadGenericCommand(short index) {
        return libInterface.PW_iPPGenericCMD(index);
    }

    public short PINPadShowMessage(String message) {
        return libInterface.PW_iPPDisplay(message);
    }

//    short PINPadWaitForEvent() {
//        return libInterface.PW_iPPWaitEvent();
//    }
}
