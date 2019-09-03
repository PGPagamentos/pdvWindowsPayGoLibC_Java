package br.com.paygo;

import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.interop.LibFunctions;
import br.com.paygo.interop.Transaction;

public class App
{
    private static final String path = ".";

    public static void main(String[] args) {
        PWRet returnedCode;

        try {
            returnedCode = LibFunctions.init(path);
            System.out.println("=> PW_iInit: " + returnedCode.toString());

            Transaction transaction = new Transaction(PWOper.INSTALL);

            returnedCode = transaction.start();
            System.out.println("=> PW_iNewTransac: " + returnedCode.toString());

            transaction.addMandatoryParams();

            do {
                returnedCode = transaction.executeTransaction();
                System.out.println("=> PW_iExecTransac: " + returnedCode);

                if (returnedCode == PWRet.MOREDATA) {
                    transaction.retrieveMoreData();
                }

            } while (returnedCode == PWRet.MOREDATA);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
