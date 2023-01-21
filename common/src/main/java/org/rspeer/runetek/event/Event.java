package org.rspeer.runetek.event;

import java.util.EventObject;

public abstract class Event<T> extends EventObject {

    private final long time;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param delay How long to delay the event before dispatching it (milliseconds)
     * @throws IllegalArgumentException if source is null.
     */
    public Event(T source, long delay) {
        super(source);
        time = System.currentTimeMillis() + delay;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public Event(T source) {
        this(source, 0);
    }

    public long getTime() {
        return time;
    }

    public abstract void forward(EventListener listener);

    @Override
    public T getSource() {
        return (T) super.getSource();
    }
}
