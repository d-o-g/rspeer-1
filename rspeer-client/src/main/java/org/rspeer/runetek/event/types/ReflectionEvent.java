package org.rspeer.runetek.event.types;

import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.ReflectionListener;

import java.lang.reflect.Method;

/**
 * Created by Zachary Herridge on 8/23/2018.
 */
public final class ReflectionEvent extends Event {

    private final Method target;
    private final Object[] args;

    public ReflectionEvent(Method target, Object[] args) {
        super("Static");
        this.target = target;
        this.args = args;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ReflectionListener) {
            ((ReflectionListener) listener).notify(this);
        }
    }

    public Method getTarget() {
        return target;
    }

    public Object[] getArgs() {
        return args;
    }
}
