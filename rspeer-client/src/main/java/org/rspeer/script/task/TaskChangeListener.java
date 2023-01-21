package org.rspeer.script.task;

/**
 * Created by Spencer on 30/06/2018.
 */
public interface TaskChangeListener {
    void notify(Task prev, Task curr);
}
