package br.com.paygo;

import br.com.paygo.enums.PWOper;
import br.com.paygo.enums.PWRet;
import br.com.paygo.interop.Confirmation;
import br.com.paygo.interop.Transaction;
import br.com.paygo.ui.SwingInterface;
import br.com.paygo.ui.UserInterface;

public class App
{
    public static void main(String[] args) {
        UserInterface userInterface = new SwingInterface();
//        UserInterface userInterface = new CMDInterface();
    }

    /*
    TODO:
    Implementar método para permitir o acesso direto à confirmação de uma transação
    pendente, sem a necessidade de executar uma venda para, no final, fazer a
    confirmação da transação pendente.
     */
    public static void confirmPendingTransaction() {
        try {
            Transaction transaction = new Transaction(PWOper.SALE, null);
            Confirmation confirmation = new Confirmation(transaction);

            PWRet returnedCode = confirmation.executeConfirmationProcess(true);

            System.out.println("=> PW_iConfirmation: " + returnedCode);
        } catch (Exception e) {
            System.out.println("Erro ao confirmar transação pendente.");
        }
    }
}
