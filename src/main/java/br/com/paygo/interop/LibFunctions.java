package br.com.paygo.interop;

import br.com.paygo.enums.*;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.ShortByReference;

import java.util.LinkedList;

public class LibFunctions {
    static private PGWebLib pgWebLib = new PGWebLib();

    /**
     * Esta função é utilizada para inicializar a biblioteca, e retorna imediatamente.
     * Deve ser garantido que uma chamada dela retorne PWRET_OK antes de chamar qualquer outra função.
     */
    public static PWRet init(String path) {
        return PWRet.valueOf(pgWebLib.init(path));
    }

    /**
     * Esta função deve ser chamada para iniciar uma nova transação através do Pay&Go Web, e retorna imediatamente.
     */
    static PWRet newTransaction(PWOper operation) {
        return PWRet.valueOf(pgWebLib.newTransaction(operation.getValue()));
    }

    /**
     * Esta função é utilizada para alimentar a biblioteca com as informações da transação a ser realizada,
     * e retorna imediatamente. Estas informações podem ser:
     *  - Pré-fixadas na Automação;
     *  - Capturadas do operador pela Automação antes do acionamento do Pay&Go Web;
     *  - Capturadas do operador após solicitação pelo Pay&Go Web (retorno PW_MOREDATA por PW_iExecTransac).
     */
    static PWRet addParam(PWInfo param, String paramValue) {
        return PWRet.valueOf(pgWebLib.addParam(param.getValue(), paramValue));
    }

    /**
     * Esta função pode ser chamada para obter informações que resultaram da transação efetuada,
     * independentemente de ter sido bem ou mal sucedida, e retorna imediatamente.
     */
    public static PWRet getResult(PWInfo param, byte[] paramValue) {
        return PWRet.valueOf(pgWebLib.getResult(param.getValue(), paramValue));
    }

    static PWRet getResult(int paramCode, byte[] paramValue) {
        return PWRet.valueOf(pgWebLib.getResult(paramCode, paramValue));
    }

    /**
     * Esta função tenta realizar uma transação através do Pay&Go Web, utilizando os parâmetros
     * previamente definidos através de PW_iAddParam. Caso algum dado adicional precise ser informado,
     * o retorno será PWRET_MOREDATA e o parâmetro pvstParam retornará informações dos dados que
     * ainda devem ser capturados.
     * Esta função, por se comunicar com a infraestrutura Pay&Go Web, pode demorar alguns segundos
     * para retornar.
     */
    static PWRet executeTransaction(PWGetData[] getData, ShortByReference numParams) {
        return PWRet.valueOf(pgWebLib.executeTransaction(getData, numParams));
    }

    /**
     * Esta função informa ao Pay&Go Web o status final da transação em curso (confirmada ou desfeita).
     */
    static PWRet confirmTransaction(PWCnf confirmationType, LinkedList<String> params) {
        return PWRet.valueOf(pgWebLib.confirmTransaction(confirmationType, params.get(0), params.get(1), params.get(2), params.get(3), params.get(4)));
    }

    /**
     * Esta função é utilizada para fazer uma remoção de cartão do PIN-pad.
     */
    static PWRet removeCardFromPINPad() {
        return PWRet.valueOf(pgWebLib.PINPadRemoveCard());
    }

    /**
     * Esta função deverá ser chamada em “loop” até que seja retornado PWRET_OK (ou um erro fatal). Nesse
     * “loop”, caso o retorno seja PWRET_DISPLAY o ponto de captura deverá atualizar o “display” com as
     * mensagens recebidas da biblioteca.
     */
    static PWRet eventLoop(byte[] displayMessage) {
        return PWRet.valueOf(pgWebLib.PINPadEventLoop(displayMessage, displayMessage.length));
    }

    /**
     * Esta função pode ser utilizada pela Automação para interromper uma captura de dados no PIN-pad
     * em curso, e retorna imediatamente.
     */
    static PWRet abortTransaction() {
        return PWRet.valueOf(pgWebLib.PINPadAbort());
    }
    
    /**
     * Esta função pode ser utilizada pela Automação para interromper uma captura de dados no PIN-pad
     * em curso, e retorna imediatamente.
     */
    public static PWRet pwGetOperations(byte value, PWOperations[] pwOpers, ShortByReference numParams) {
    	return PWRet.valueOf(pgWebLib.getOperations(value, pwOpers, numParams));
    }

    /**
     * Esta função é utilizada para realizar a leitura de um cartão (magnético, com chip com contato,
     * ou sem contato) no PIN-pad.
     */
    static PWRet getCardFromPINPad(short index) {
        return PWRet.valueOf(pgWebLib.PINPadGetCard(index));
    }

    /**
     * Esta função é utilizada para realizar o processamento off-line (antes da comunicação com o Provedor)
     * de um cartão com chip no PIN-pad.
     */
    static PWRet offlineCardProcessing(short index) {
        return PWRet.valueOf(pgWebLib.PINPadChipCardOfflineProcessing(index));
    }

    /**
     * Esta função é utilizada para finalizar o processamento on-line (após comunicação com o Provedor)
     * de um cartão com chip no PIN-pad.
     */
    static PWRet finishOfflineProcessing(short index) {
        return PWRet.valueOf(pgWebLib.PINPadChipCardFinishOfflineProcessing(index));
    }

    /**
     * Esta função é utilizada para realizar a captura no PIN-pad da senha (ou outro dado criptografado)
     * do Cliente.
     */
    static PWRet getPIN(short index) {
        return PWRet.valueOf(pgWebLib.PINPadGetPIN(index));
    }

    /**
     * Esta função é utilizada para obter o PIN block gerado a partir de um dado digitado pelo usuário no PIN-pad.
     */
    public static PWRet getPINBlock(String key, int minLength, int maxLength, int seconds, String promptMessage, byte[] data) {
        return PWRet.valueOf(pgWebLib.PINPadGetPINBlock(key, minLength, maxLength, seconds, promptMessage, data));
    }

    /**
     * Esta função é utilizada para obter um dado digitado pelo portador do cartão no PIN-pad.
     */
    public static PWRet getUserDataOnPINPad(PWUserDataMessage userDataMessage, int minSize, int maxSize, int timeout, byte[] value) {
        return PWRet.valueOf(pgWebLib.PINPadGetUserData(userDataMessage, (byte)minSize, (byte)maxSize, (short)timeout, value));
    }

    /**
     * Esta função é utilizada para apresentar uma mensagem no PIN-pad.
     */
    public static PWRet showMessageOnPINPad(String message) {
        return PWRet.valueOf(pgWebLib.PINPadShowMessage(message));
    }

    /**
     * Esta função é utilizada para aguardar a ocorrência de um evento no PIN-pad.
     */
    public static PWRet waitEventOnPINPad(LongByReference event) {
        return PWRet.valueOf(pgWebLib.PINPadWaitEvent(event));
    }
    
    static PWRet ppConfirmationData(short index) {
    	return PWRet.valueOf(pgWebLib.PINPadRequestConfirmation(index));
    }
    
    static PWRet ppPositiveConfirmation (short index) {
    	return PWRet.valueOf(pgWebLib.PINPadPositiveConfirmation(index));
    }
    
    static PWRet ppGetData (short index) {
    	return PWRet.valueOf(pgWebLib.PINPadGetData(index));
    }
    
    static PWRet ppGenericCommand (short index) {
    	return PWRet.valueOf(pgWebLib.PINPadGenericCommand(index));
    }

}
