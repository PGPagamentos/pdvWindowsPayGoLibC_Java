package br.com.paygo.interop;

import br.com.paygo.enums.PWRet;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.ui.UserInterface;

public class EventLoop {

    static PWRet execute(UserInterface userInterface, byte[] displayMessage) {
        PWRet eventLoopResponse;

        do {
            eventLoopResponse = LibFunctions.eventLoop(displayMessage);

            if (eventLoopResponse == PWRet.DISPLAY) {
                String message = TextFormatter.formatByteMessage(displayMessage);
                userInterface.logInfo("\n\n" + message + "\n\n");
            }

            if(eventLoopResponse == PWRet.CANCEL || eventLoopResponse == PWRet.TIMEOUT) {
                break;
            }
        } while (eventLoopResponse != PWRet.OK);

        return eventLoopResponse;
    }
}
