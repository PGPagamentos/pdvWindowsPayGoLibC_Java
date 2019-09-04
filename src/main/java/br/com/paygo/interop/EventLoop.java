package br.com.paygo.interop;

import br.com.paygo.enums.PWRet;
import br.com.paygo.exception.InvalidReturnTypeException;
import br.com.paygo.helper.TextFormatter;

class EventLoop {

    static PWRet execute(byte[] displayMessage) throws InvalidReturnTypeException {
        String result;
        PWRet eventLoopResponse;

        do {
            eventLoopResponse = LibFunctions.eventLoop(displayMessage);

            if (eventLoopResponse == PWRet.DISPLAY) {
                String message = TextFormatter.formatByteMessage(displayMessage);
                System.out.println("Transaction Message: " + message + "\n");
            }

            result = eventLoopResponse == PWRet.NOTHING ? "." : "\n" + eventLoopResponse;
            System.out.print(result);

            if(eventLoopResponse == PWRet.CANCEL) {
                break;
            }
        } while (eventLoopResponse != PWRet.OK);

        return eventLoopResponse;
    }
}
