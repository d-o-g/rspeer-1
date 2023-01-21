package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.BotCommandListener;

public final class BotCommandEvent extends Event {

    private final String message;

    public BotCommandEvent(String message) {
        super("Static");
        this.message = message;
        System.out.println("Command: " + message);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof BotCommandListener) {
            ((BotCommandListener) listener).notify(this);
        }
    }

    public String getMessage() {
        return message;
    }
}
