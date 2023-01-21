package org.rspeer.runetek.api.movement.pathfinding.executor.custom;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.component.tab.Spell;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.pathfinding.executor.PathExecutor;
import org.rspeer.runetek.api.movement.pathfinding.hpa.data.EdgeInteraction;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;

import java.util.HashMap;

@Deprecated
public class TeleportExecutor implements CustomEdgeExecutor {

    private static final String SPELL_NAME = "SPELL_NAME";
    private static final String WILDERNESS = "WILDERNESS";
    private static final String HOME = "HOME_TELEPORT";

    private static final HashMap<Spell, Integer> TELEPORT_TABLETS = new HashMap<Spell, Integer>() {{
        put(Spell.Modern.VARROCK_TELEPORT, 8007);
        put(Spell.Modern.LUMBRIDGE_TELEPORT, 8008);
        put(Spell.Modern.FALADOR_TELEPORT, 8009);
        put(Spell.Modern.CAMELOT_TELEPORT, 8010);
        put(Spell.Modern.ARDOUGNE_TELEPORT, 8011);
        put(Spell.Modern.WATCHTOWER_TELEPORT, 8012);
    }};

    @Override
    public boolean handleCustomInteraction(PathExecutor pathExecutor, HpaPath hpaPath, HpaEdge hpaEdge, EdgeInteraction interaction) {
        String spellName = (String) extractFirstDataValue(SPELL_NAME, hpaEdge);
        Boolean wilderness = (Boolean) extractFirstDataValue(WILDERNESS, hpaEdge);
        if (spellName == null) {
            return false;
        }

        Spell spell = getSpell(spellName);
        if (spell == null) {
            return false;
        }

        return TELEPORT_TABLETS.containsKey(spell) && handleTablet(TELEPORT_TABLETS.get(spell))
                || handleSpell(spell, wilderness);
    }

    private boolean handleSpell(Spell spell, Boolean wilderness) {
        log("Handling teleport using magic spell " + spell + ".");
        Position before = Players.getLocal().getPosition();
        if (Magic.cast(spell)) {
            boolean result = true;
            if (wilderness != null && wilderness && Time.sleepUntil(Dialog::isViewingChatOptions, 1200)) {
                result = Dialog.process(0);
            }

            boolean home = spell.toString().contains(HOME);
            if (home) {
                result &= Time.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 1200)
                        && Time.sleepUntil(() -> Players.getLocal().getAnimation() == -1, 10000);
            } else {
                result &= Time.sleepUntil(() -> !before.equals(Players.getLocal().getPosition()), 250, 2500);
            }

            return result;
        }

        return false;
    }


    private boolean handleTablet(int tablet) {
        log("Handling teleport using tablet " + tablet + ".");
        Item item = Inventory.getFirst(tablet);
        Position before = Players.getLocal().getPosition();
        return item != null && item.interact("Break")
                && Time.sleepUntil(() -> !before.equals(Players.getLocal().getPosition()), 250, 2500);
    }

    private Spell getSpell(String name) {
        switch (Magic.getSpellBook()) {
            case 0:
                return Spell.Modern.valueOf(name.toUpperCase());

            case 1:
                return Spell.Ancient.valueOf(name.toUpperCase());

            case 2:
                return Spell.Lunar.valueOf(name.toUpperCase());

            case 3:
                return Spell.Necromancy.valueOf(name.toUpperCase());
        }

        log("Current spellbook was not detected correctly");

        return null;
    }
}
