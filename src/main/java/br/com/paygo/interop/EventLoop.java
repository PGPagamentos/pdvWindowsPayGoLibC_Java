package br.com.paygo.interop;

import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import br.com.paygo.helper.TextFormatter;

class EventLoop {

    static PWRet execute(byte[] displayMessage) throws InvalidReturnTypeException {
        PWRet eventLoopResponse;

        do {
            eventLoopResponse = LibFunctions.eventLoop(displayMessage);

            if (eventLoopResponse == PWRet.DISPLAY) {
                String message = TextFormatter.formatByteMessage(displayMessage);
                System.out.println("\n\n\n\t" + message + "\n\n");
            }

            if(eventLoopResponse == PWRet.CANCEL) {
                break;
            }
        } while (eventLoopResponse != PWRet.OK);

        return eventLoopResponse;
    }
}
