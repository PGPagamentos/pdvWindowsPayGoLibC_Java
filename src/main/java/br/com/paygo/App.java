package br.com.paygo;

import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.interop.LibFunctions;
import br.com.paygo.interop.Transaction;

public class App
{
    private static final String path = ".";

    public static void main(String[] args) {
        init();
        sale();
    }

    private static void init() {
        try {
            PWRet returnedCode = LibFunctions.init(path);
            System.out.println("=> PW_iInit: " + returnedCode.toString());
        } catch (Exception e) {
            System.out.println("Erro ao iniciar a comunicação.");
            System.exit(-1);
        }
    }

    private static void install() {
        PWRet returnedCode;

        try {
            Transaction transaction = new Transaction(PWOper.INSTALL);
            returnedCode = transaction.start();

            System.out.println("=> PW_iNewTransac: " + returnedCode.toString());

            do {
                returnedCode = transaction.executeTransaction();
                System.out.println("=> PW_iExecTransac: " + returnedCode);

                transaction.showTransactionStatus();

                if (returnedCode == PWRet.MOREDATA) {
                    transaction.retrieveMoreData();
                }
            } while (returnedCode == PWRet.MOREDATA);

            if (returnedCode == PWRet.OK) {
                System.out.println("\n\n=> INSTALAÇÃO CONCLUÍDA <=");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void sale() {
        PWRet returnedCode;

        try {
            Transaction transaction = new Transaction(PWOper.SALE);
            returnedCode = transaction.start();

            System.out.println("=> PW_iNewTransac: " + returnedCode.toString());

            if (returnedCode == PWRet.OK) {
                do {
                    returnedCode = transaction.executeTransaction();
                    System.out.println("=> PW_iExecTransac: " + returnedCode);

                    transaction.showTransactionStatus();

                    if (returnedCode == PWRet.MOREDATA) {
                        transaction.retrieveMoreData();
                    }
                } while (returnedCode == PWRet.MOREDATA);
            } else {
                returnedCode = transaction.getResult(PWInfo.RESULTMSG);
                System.out.println("=> PW_iGetResult: " + returnedCode + "\n\t" + transaction.getValue(true));
            }

            if (returnedCode == PWRet.OK) {
                System.out.println("\n\n=> VENDA CONCLUÍDA <=");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}
