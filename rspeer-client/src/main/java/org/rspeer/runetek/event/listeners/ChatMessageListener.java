package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;

public interface ChatMessageListener extends EventListener {
    void notify(ChatMessageEvent event);
}
