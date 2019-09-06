package br.com.paygo;

import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.interop.Confirmation;
import br.com.paygo.interop.LibFunctions;
import br.com.paygo.interop.Transaction;

public class App
{
    private static final String path = ".";
    private static Transaction transaction;

    public static void main(String[] args) {
        init();
//        install();
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
            transaction = new Transaction(PWOper.INSTALL);
            returnedCode = transaction.start();

            System.out.println("=> PW_iNewTransac: " + returnedCode.toString());

            do {
                returnedCode = transaction.executeTransaction();
                System.out.println("=> PW_iExecTransac: " + returnedCode);

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
            transaction = new Transaction(PWOper.SALE);
            returnedCode = transaction.start();

            System.out.println("=> PW_iNewTransac: " + returnedCode.toString());

            if (returnedCode == PWRet.OK) {
                do {
                    returnedCode = transaction.executeTransaction();
                    System.out.println("=> PW_iExecTransac: " + returnedCode);

                    if (returnedCode == PWRet.MOREDATA) {
                        transaction.retrieveMoreData();
                    }
                } while (returnedCode == PWRet.MOREDATA);

                if (returnedCode == PWRet.OK) {
                    returnedCode = transaction.finalizeTransaction();
                } else if(returnedCode == PWRet.FROMHOSTPENDTRN) {
                    System.out.println("===========================================" +
                            "== ERRO - EXITSTE UMA TRANSAÇÃO PENDENTE ==" +
                            "===========================================");
                    Confirmation confirmation = new Confirmation(transaction);
                    returnedCode = confirmation.executeConfirmationProcess(true);

                    System.out.println("=> PW_iConfirmation: " + returnedCode);
                }
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

    /*
    TODO:
    Implementar método para permitir o acesso direto à confirmação de uma transação
    pendente, sem a necessidade de executar uma venda para, no final, fazer a
    confirmação da transação pendente.
     */
    public static void confirmPendingTransaction() {
        try {
            Transaction transaction = new Transaction(PWOper.SALE);
            Confirmation confirmation = new Confirmation(transaction);

            PWRet returnedCode = confirmation.executeConfirmationProcess(true);

            System.out.println("=> PW_iConfirmation: " + returnedCode);
        } catch (Exception e) {
            System.out.println("Erro ao confirmar transação pendente.");
        }
    }
}
