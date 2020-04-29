package br.com.paygo.interop;

import br.com.paygo.enums.PWRet;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.ui.UserInterface;
import br.com.paygo.ui.UserInterfaceMessage;

/**
 * Função utilizada para aguardar um retorno da biblioteca PGWebLib ou do PIN-pad
 */
public class EventLoop {

    static PWRet execute(UserInterface userInterface, byte[] displayMessage) {
        PWRet eventLoopResponse;

        do {
        	// Chama o eventloop para interação com o PIN-pad
            eventLoopResponse = LibFunctions.eventLoop(displayMessage);

            /* Caso o eventloop sinalize a exibição de uma mensagem */
            if (eventLoopResponse == PWRet.DISPLAY) {
                String message = TextFormatter.formatByteMessage(displayMessage);
                userInterface.logInfo("\n\n" + message + "\n\n");
            }
            /* Qualquer tipo de erro, interrompe o fluxo e retorna */            
            if(eventLoopResponse != PWRet.OK && eventLoopResponse != PWRet.DISPLAY && eventLoopResponse != PWRet.NOTHING) {
                break;
            }
            
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
        } while (eventLoopResponse != PWRet.OK);

        return eventLoopResponse;
    }
    
    /**
     * Eventloop usado nas funções que necessitam desta função fora do fluxo transacional
     * Por isso foi criada a interface de message, para que seja implementado somente a exibição
     * de "callback"
     * */
    public static PWRet execute(UserInterfaceMessage userInterface, byte[] displayMessage) {
        PWRet eventLoopResponse;

        do {
        	// Chama o eventloop para interação com o PIN-pad
            eventLoopResponse = LibFunctions.eventLoop(displayMessage);

            /* Caso o eventloop sinalize a exibição de uma mensagem */
            if (eventLoopResponse == PWRet.DISPLAY) {
                String message = TextFormatter.formatByteMessage(displayMessage);
                if(userInterface != null)
                	userInterface.showMessage("\n\n" + message + "\n\n");
            }
            /* Qualquer tipo de erro, interrompe o fluxo e retorna */            
            if(eventLoopResponse != PWRet.OK && eventLoopResponse != PWRet.DISPLAY && eventLoopResponse != PWRet.NOTHING) {
                break;
            }
            
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
        } while (eventLoopResponse != PWRet.OK);

        return eventLoopResponse;
    }
    
}
