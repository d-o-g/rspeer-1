package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.BotCommandEvent;

public interface BotCommandListener extends EventListener {
    void notify(BotCommandEvent e);
}
