package org.rspeer.runetek.event;

import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.runetek.event.listeners.KeyInputListener;
import org.rspeer.runetek.event.listeners.MouseInputListener;
import org.rspeer.runetek.event.listeners.TickListener;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class EventDispatcher {

    private final Queue<Event> delayed;
    private final List<EventListener> listeners;

    private final Object mutex;

    private final ExecutorService immediate;

    private boolean active = true;

    public EventDispatcher() {
        delayed = new LinkedList<>();
        listeners = new CopyOnWriteArrayList<>();
        mutex = new Object();

        RsPeerExecutor.scheduleAtFixedRate(this::process, 0, 20, TimeUnit.MILLISECONDS);

        immediate = Executors.newCachedThreadPool();
    }

    public void register(EventListener listener) {
        synchronized (mutex) {
            listeners.add(listener);
        }
    }

    public void deregister(EventListener listener) {
        synchronized (mutex) {
            listeners.remove(listener);
        }
    }

    public void immediateInput(InputEvent event) {
        synchronized (mutex) {
            if (active) {
                for (EventListener listener : listeners) {
                    if (event instanceof MouseEvent && listener instanceof MouseInputListener) {
                        ((MouseInputListener) listener).notify((MouseEvent) event);
                    } else if (event instanceof KeyEvent && listener instanceof KeyInputListener) {
                        ((KeyInputListener) listener).notify((KeyEvent) event);
                    }
                }
            }
        }
    }

    public void immediate(Event event) {
        synchronized (mutex) {
            if (active) {
                for (EventListener listener : listeners) {
                    if (listener instanceof TickListener) {
                        immediate.execute(() -> event.forward(listener));
                    } else {
                        event.forward(listener);
                    }
                }
            }
        }
    }

    public void delay(Event event) {
        synchronized (delayed) {
            delayed.add(event);
            delayed.notify();
        }
    }

    private Event poll() {
        while (true) {
            if (!active) {
                continue;
            }
            synchronized (delayed) {
                while (active && delayed.isEmpty()) {
                    try {
                        delayed.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                Event event = delayed.peek();
                if (event == null || event.getTime() > System.currentTimeMillis()) {
                    return null;
                }
                delayed.remove(event);
                return event;
            }
        }
    }

    public void clear() {
        delayed.clear();
    }

    public void process() {
        Event event = poll();
        while (event != null) {
            for (EventListener listener : listeners) {
                event.forward(listener);
            }
            event = poll();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        synchronized (delayed) {
            delayed.notify();
        }
    }

    public boolean isRegistered(EventListener e) {
        return listeners.contains(e);
    }
}
