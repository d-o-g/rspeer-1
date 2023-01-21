package org.rspeer.script.events;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.TargetListener;
import org.rspeer.runetek.event.types.TargetEvent;
import org.rspeer.script.GameAccount;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptBlockingEvent;
import org.rspeer.ui.Log;

import java.time.Duration;

public final class DismissRandoms extends ScriptBlockingEvent implements TargetListener {

    private final int percentage;
    private Duration randomWait;
    private StopWatch timer;
    private Npc random;
    private GameAccount account;

    public DismissRandoms(Script ctx) {
        super(ctx);
        this.account = ctx.getAccount();
        if (account == null) {
            this.percentage = 0;
        } else {
            this.percentage = ctx.getAccount().getDismiss();
        }
    }

    @Override
    public boolean validate() {
        return percentage > 0
                && random != null
                && timer.exceeds(randomWait);
    }

    @Override
    public void process() {
        int roll = Random.nextInt(100);
        if (roll < percentage) {
            String name = random.getName();
            for (int i = 0; i < 5 && random.getProvider() != null; i++) {
                random.interact("Dismiss");
                Time.sleepUntil(() -> random.getProvider() == null, 600);
            }

            Log.fine("Dismissed " + name + " event");
        }

        random = null;
    }

    @Override
    public void notify(TargetEvent e) {
        if (account == null) {
            return;
        }

        PathingEntity src = e.getSource();
        if (!(src instanceof Npc) || e.getTarget() == null) {
            return;
        }

        Npc npc = (Npc) src;
        if (!npc.containsAction("Dismiss")) {
            return;
        }

        if (e.getTarget() != Players.getLocal()) {
            return;
        }

        String xpPref = account.getXpPreference();
        if (!xpPref.equals("None")) {
            if (src.getName().equals("Dunce") || src.getName().equals("Genie")) {
                return;
            }
        }

        random = npc;
        timer = StopWatch.start();
        randomWait = Duration.ofMillis(Random.nextLong(500, 2500));
    }
}
