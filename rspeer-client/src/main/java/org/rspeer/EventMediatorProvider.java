package org.rspeer;

import org.rspeer.runetek.event.EventMediator;
import org.rspeer.ui.Log;

public class EventMediatorProvider {

    public static EventMediator provide() {
        return new EventMediator(EventMediatorProvider::onException);
    }

    private static void onException(Exception e) {
        Log.severe(e);
    }
}
