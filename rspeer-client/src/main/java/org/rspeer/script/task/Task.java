package org.rspeer.script.task;

/**
 * Created by Spencer on 22/05/2018.
 */
public abstract class Task implements Executable, Condition, Comparable<Task> {

    public int getPriority() {
        return Short.MAX_VALUE;
    }

    @Override
    public final int compareTo(Task o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
