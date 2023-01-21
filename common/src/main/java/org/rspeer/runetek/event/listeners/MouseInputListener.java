package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;

import java.awt.event.MouseEvent;

public interface MouseInputListener extends EventListener {
    void notify(MouseEvent e);
}
