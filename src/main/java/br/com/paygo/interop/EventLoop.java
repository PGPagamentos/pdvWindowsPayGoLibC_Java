package br.com.paygo.interop;

import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.ui.UserInterface;

class EventLoop {

    static PWRet execute(UserInterface userInterface, byte[] displayMessage) throws InvalidReturnTypeException {
        PWRet eventLoopResponse;

        do {
            eventLoopResponse = LibFunctions.eventLoop(displayMessage);

            if (eventLoopResponse == PWRet.DISPLAY) {
                String message = TextFormatter.formatByteMessage(displayMessage);
                userInterface.logInfo("\n\n" + message + "\n\n");
            }

            if(eventLoopResponse == PWRet.CANCEL) {
                break;
            }
        } while (eventLoopResponse != PWRet.OK);

        return eventLoopResponse;
    }
}
