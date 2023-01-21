package org.rspeer.runetek.event.listeners;

import org.rspeer.runetek.event.EventListener;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface KeyInputListener extends EventListener {
    void notify(KeyEvent e);
}
