package org.rspeer.runetek.event;

public class EventDispatcherProvider {

    private static EventDispatcherProvider instance;

    public static EventDispatcher provide() {
        if(instance == null) {
            instance = new EventDispatcherProvider();
        }
        return instance.dispatcher;
    }

    private EventDispatcher dispatcher = new EventDispatcher();

    private EventDispatcherProvider() {
    }

}
