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

    short init(String path) {
        return libInterface.PW_iInit(path);
    }

    short newTransaction(int operation) {
        return libInterface.PW_iNewTransac(operation);
    }

    short addParam(int param, String paramValue) {
        return libInterface.PW_iAddParam(param, paramValue);
    }

    short getResult(PWInfo param, byte[] paramValue) {
        return libInterface.PW_iGetResult(param.getValue(), paramValue, paramValue.length);
    }

    short executeTransaction(PWGetData[] getData, ShortByReference numParams) {
        return libInterface.PW_iExecTransac(getData, numParams);
    }

    short confirmTransaction(PWCnf confirmationType, String transactionLocalRef, String transactionPGWRef,
                                    String transactionProviderRef, String storeId, String providerName) {

        return libInterface.PW_iConfirmation(confirmationType.getValue(), transactionLocalRef, transactionPGWRef,
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

    short PINPadGetPINBlock(String key, int minLength, int maxLength, int seconds, String promptMessage, byte[] data) {
        return libInterface.PW_iPPGetPINBlock((byte) 12, key, (byte)minLength, (byte)maxLength, (short)seconds, promptMessage, data);
    }

    short PINPadGetData(short index) {
        return libInterface.PW_iPPGetData(index);
    }

    short PINPadGetUserData(PWUserDataMessage userDataMessage, byte minLength, byte maxLength, short timeout, byte[] value) {
        short userDataCode = (short) (userDataMessage.ordinal() + 1);
        return libInterface.PW_iPPGetUserData(userDataCode, minLength, maxLength, timeout, value);
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
