package org.rspeer.script.events;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptBlockingEvent;
import org.rspeer.script.ScriptMeta;

/**
 * Created by Spencer on 05/05/2018.
 */
@ScriptMeta(name = "Welcome screen handler", developer = "Spencer", desc = "Clicks the play button on the welcome screen")
public final class WelcomeScreen extends ScriptBlockingEvent {

    private static final int[] WELCOME_SCREEN_INDICES = new int[]{
            InterfaceComposite.WELCOME_SCREEN.getGroup(),
            InterfaceComposite.WELCOME_SCREEN_2.getGroup(),
            InterfaceComposite.WELCOME_SCREEN_3.getGroup()
    };

    public static final InterfaceAddress WELCOME_ADDRESS = new InterfaceAddress(() -> {
        return Interfaces.getFirst(WELCOME_SCREEN_INDICES, ic -> ic.getText().toLowerCase().contains("click here to play"), true);
    });

    public WelcomeScreen(Script ctx) {
        super(ctx);
    }

    @Override
    public boolean validate() {
        return Interfaces.isVisible(WELCOME_ADDRESS);
    }

    @Override
    public void process() {
        InterfaceComponent clickToPlay = WELCOME_ADDRESS.resolve();
        if (clickToPlay != null && clickToPlay.isVisible()) {
            clickToPlay = Interfaces.getFirst(clickToPlay.getRootIndex(), x -> x.containsAction("play"));
            Time.sleep(320, 553);
            if (clickToPlay != null && clickToPlay.interact("Play")) {
                Time.sleepUntilForDuration(() -> !validate(), 600, 1200);
            }
        }
        Time.sleep(900, 1500);
    }
}
