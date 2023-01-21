package org.rspeer.script.events.genie;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.TargetListener;
import org.rspeer.runetek.event.types.TargetEvent;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptBlockingEvent;
import org.rspeer.ui.Log;

import java.util.function.BooleanSupplier;

public final class GenieSolver extends ScriptBlockingEvent implements TargetListener {

    private static final BooleanSupplier IS_LAMP_INTERFACE_OPEN = () -> Interfaces.getComponent(ExpLampInterface.PARENT.getId(), ExpLampInterface.TOP_TEXT.getId()) != null;

    private final String preference;
    private Npc src;

    public GenieSolver(Script ctx) {
        super(ctx);
        if (ctx.getAccount() == null) {
            this.preference = "None";
        } else {
            this.preference = ctx.getAccount().getXpPreference();
        }
    }

    @Override
    public boolean validate() {
        return !preference.equals("None")
                && Game.isLoggedIn()
                && !Players.getLocal().isHealthBarVisible()
                && (src != null || Inventory.contains("Lamp"));
    }

    @Override
    public void process() {
        if (src != null) {
            for (int i = 0; i < 5 && src.getProvider() != null; i++) {
                src.interact("Talk-to");
                Time.sleepUntil(() -> src.getProvider() == null, 600);
            }

            src = null;
        } else {
            if (IS_LAMP_INTERFACE_OPEN.getAsBoolean()) {
                ExpLampInterface skill;
                if (preference.equals("Random")) {
                    skill = Random.nextElement(ExpLampInterface.getNonCombatSkills());
                } else {
                    skill = ExpLampInterface.getSkill(preference);
                }

                if (skill == null) {
                    skill = ExpLampInterface.RUNECRAFTING;
                }

                InterfaceComponent skillBtn = Interfaces.getComponent(ExpLampInterface.PARENT.getId(), skill.getId());
                if (skillBtn != null && skillBtn.interact(skill.getAction())) {
                    Time.sleep(600, 1200);
                    InterfaceComponent confirm = Interfaces.getComponent(ExpLampInterface.PARENT.getId(), ExpLampInterface.CONFIRM.getId());
                    if (confirm != null && confirm.interact(ExpLampInterface.CONFIRM.getAction())) {
                        Time.sleepUntil(() -> !IS_LAMP_INTERFACE_OPEN.getAsBoolean(), 1200);
                    }
                }
            } else {
                Item lamp = Inventory.getFirst("Lamp");
                if (lamp != null && lamp.interact("Rub")) {
                    Time.sleepUntil(IS_LAMP_INTERFACE_OPEN, 1200);
                }
            }
        }
    }

    @Override
    public void notify(TargetEvent e) {
        if (preference.equals("None")) {
            return;
        }

        PathingEntity src = e.getSource();
        if (src instanceof Npc) {
            Npc npc = (Npc) src;
            if (e.getTarget() == Players.getLocal()
                    && npc.getName().equals("Genie")) {
                this.src = npc;
            }
        }
    }
}
