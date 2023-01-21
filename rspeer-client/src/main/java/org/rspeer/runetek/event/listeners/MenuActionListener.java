package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.types.MenuActionEvent;

public interface MenuActionListener extends EventListener {
    void notify(MenuActionEvent event);
}
