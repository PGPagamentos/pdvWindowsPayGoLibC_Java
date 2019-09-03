package br.com.paygo.interop;

import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import com.sun.jna.ptr.ShortByReference;

public class LibFunctions {
    static private PGWebLib pgWebLib = new PGWebLib();

    public static PWRet init(String path) throws Exception {
        return PWRet.valueOf(pgWebLib.init(path));
    }

    public static PWRet newTransaction(PWOper operation) throws Exception {
        return PWRet.valueOf(pgWebLib.newTransaction(operation.getValue()));
    }

    public static PWRet addParam(PWInfo param, String paramValue) throws Exception {
        return PWRet.valueOf(pgWebLib.addParam(param.getValue(), paramValue));
    }

    public static PWRet getResult(PWInfo param) throws Exception {
        return PWRet.valueOf(pgWebLib.getResult(param));
    }

    public static PWRet executeTransaction(PWGetData[] getData, ShortByReference numParams) throws Exception {
        return PWRet.valueOf(pgWebLib.executeTransaction(getData, numParams));
    }
}
