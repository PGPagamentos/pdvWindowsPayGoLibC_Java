package br.com.paygo.interop;

import com.sun.jna.Library;
import com.sun.jna.ptr.ShortByReference;

public interface PGWebLibMap extends Library {

    short PW_iInit(String workingDir);
    short PW_iNewTransac(int operation);
    short PW_iAddParam(int param, String paramValue);
    short PW_iExecTransac(PWGetData[] pwGetData, ShortByReference numParams);
    short PW_iGetResult(int param, byte[] value, int valueSize);
    short PW_iConfirmation(int transacResult, String transacLocalRef, String transacPGWRef,
                           String transacProviderRef, String storeId, String providerName);
    short PW_iIdleProc();
    short PW_iGetOperations(); // VERIFICAR - não implementado nas outras aplicações

    short PW_iPPEventLoop(byte[] displayMessage, int displaySize);
    short PW_iPPAbort();
    short PW_iPPGetCard(short index);
    short PW_iPPRemoveCard();
    short PW_iPPGetPIN(short index);
    short PW_iPPGetData(short index);
    short PW_iPPGetUserData(short userDataMessage, byte minLength, byte maxLength, short timeout, String value);
    short PW_iPPGoOnChip(short index);
    short PW_iPPFinishChip(short index);
    short PW_iPPConfirmData(short index);
    short PW_iPPDataConfirmation(short index);
    short PW_iPPGenericCMD(short index);

    short PW_iPPDisplay(String message);
    short PW_iPPWaitEvent(); // VERIFICAR - não implementado nas outras aplicações
    short PW_iPPPositiveConfirmation(short index); // VERIFICAR - não implementado nas outras aplicações
}
