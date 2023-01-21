package org.rspeer.script.task;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.script.Script;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Spencer on 22/05/2018.
 */
public abstract class TaskScript extends Script {

    private static final int DEFAULT_LOOP_DELAY = 300;

    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    private Task lastExecuted;
    private Task current;

    public abstract void onStart();

    @Override
    public final int loop() {
        Task current = null;
        for (Task task : tasks) {
            if (task.validate()) {
                if (task != lastExecuted) {
                    if (this instanceof TaskChangeListener) {
                        ((TaskChangeListener) this).notify(lastExecuted, task);
                    }

                    if (task instanceof TaskChangeListener) {
                        ((TaskChangeListener) task).notify(lastExecuted, task);
                    }

                    if (lastExecuted instanceof TaskChangeListener) {
                        ((TaskChangeListener) lastExecuted).notify(lastExecuted, task);
                    }
                }
                current = task;
                lastExecuted = task;
                break;
            }
        }
        return (this.current = current) != null ? current.execute() : DEFAULT_LOOP_DELAY;
    }

    /**
     * Adds a Task to the collection. Note that tasks that are prioritized should be submitted
     * prior to other tasks
     *
     * @param tasks The tasks to add
     */
    public final void submit(Task... tasks) {
        for (Task task : tasks) {
            submit(task);
        }
    }

    private void submit(Task task) {
        tasks.add(task);
        Collections.sort(tasks);
        if (task instanceof EventListener) {
            Game.getEventDispatcher().register((EventListener) task);
        }
    }

    public final void remove(Task... tasks) {
        for (Task task : tasks) {
            this.tasks.remove(task);
            if (task instanceof EventListener) {
                Game.getEventDispatcher().deregister((EventListener) task);
            }
        }
    }

    public final void setStopping(boolean stopping) {
        super.setStopping(stopping);
        if (stopping) {
            for (Task e : tasks) {
                if (e instanceof EventListener && Game.getEventDispatcher().isRegistered((EventListener) e)) {
                    Game.getEventDispatcher().deregister((EventListener) e);
                }
            }
        }
    }

    public final void removeAll() {
        this.tasks.clear();
    }

    public Task getCurrent() {
        return current;
    }
}
