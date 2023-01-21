package org.rspeer.ui.debug;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.AWTUtil;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.api.input.Mouse;
import org.rspeer.runetek.api.input.menu.ContextMenu;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.pathfinding.region.util.CollisionFlags;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.*;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;

import java.awt.*;

/**
 * Created by Spencer on 10/02/2018.
 */
public final class Debugger implements RenderListener {

    private static final Font DEFAULT = Font.getFont(Font.DIALOG);

    public void start() {
        Game.getEventDispatcher().register(this);
    }

    public void end() {
        Game.getEventDispatcher().deregister(this);
    }

    @Override
    public void notify(RenderEvent event) {
        Graphics g = event.getSource();
        drawHovered(g);
        drawLocalInfo(g);
    }

    private void drawLocalInfo(Graphics g) {
        if (Game.isLoggedIn()) {
            g.setColor(Color.WHITE);

            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            ((Graphics2D) g).setRenderingHints(rh);

            if (DEFAULT != null) {
                g.setFont(DEFAULT);
            }

            AWTUtil.drawBoldedString(g, "Game state: " + Game.getState(), 20, 30);
            AWTUtil.drawBoldedString(g, "Location: " + Players.getLocal().getPosition(), 20, 50);
            AWTUtil.drawBoldedString(g, "Scene Base: " + Scene.getBase(), 20, 70);

            Player local = Players.getLocal();
            if (local == null) {
                return;
            }

            AWTUtil.drawBoldedString(g, "Animation: " + local.getAnimation(), 20, 90);
            AWTUtil.drawBoldedString(g, "Animation frame: " + local.getAnimationFrame(), 20, 110);
            AWTUtil.drawBoldedString(g, "Stance: " + local.getStance(), 20, 130);
            AWTUtil.drawBoldedString(g, "Target: " + local.getTarget() + " (" + local.getTargetIndex() + ")", 20, 150);

            Position hint = HintArrow.getPosition();
            if (hint != null) {
                AWTUtil.drawBoldedString(g, "Hint Arrow: " + hint.getPosition(), 20, 170);
                AWTUtil.drawBoldedString(g, "Hint Type: " + HintArrow.getType(), 20, 190);
            }

            if (Tabs.isOpen(Tab.INVENTORY)) {
                drawItems(g, Inventory.getItems());
            } else if (Tabs.isOpen(Tab.EQUIPMENT)) {
                drawItems(g, Equipment.getItems());
            }

            //AWTUtil.drawBoldedString(g, CollisionFlags.toString(Scene.getCollisionFlag(local.getPosition())), local.toScreen().getX(), local.toScreen().getY());
        }
    }

    private void drawItems(Graphics g, Item... items) {
        for (Item item : items) {
            Rectangle bounds = item.getBounds();
            AWTUtil.drawBoldedString(g, String.valueOf(item.getId()), bounds.x, bounds.y);
        }
    }

    private void drawHovered(Graphics g) {
        long[] hovered = ContextMenu.getOnCursorUids();
        int count = ContextMenu.getOnCursorCount();
        for (int i = 0; i < count; i++) {
            if (i >= 1000) {
                break;
            }
            long uid = hovered[i];
            int type = (int) (uid >>> 14 & 0x3L);
            if (type == 2) {
                SceneObject obj = SceneObjects.getByUid(uid);
                if (obj != null) {
                    if (Movement.getDebug().isToggled()) {
                        Position nearestAccessiblePosition = Movement.getReachableMap().findNearestAccessiblePosition(obj);
                        if (nearestAccessiblePosition != null) {
                            Movement.getDebug().outlineTile(nearestAccessiblePosition, g, Color.GREEN);
                        }
                        g.setColor(Color.BLACK);
                    }
                    g.setColor(Color.MAGENTA);
                    drawUnderMouse(g, String.valueOf(obj.getId()) + " " + obj.getPosition() + " [Orientation: " + obj.getOrientation() + "]");
                    break;
                }
            } else if (type == 3) {
                Pickable item = Pickables.getNearest((o) -> ContextMenu.uidFor(o) == uid);
                if (item != null) {
                    g.setColor(Color.RED);
                    drawUnderMouse(g, String.valueOf(item.getId()));
                    break;
                }
            } else if (type == 1) {
                Npc npc = Npcs.getNearest((o) -> ContextMenu.uidFor(o) == uid);
                if (npc != null) {
                    g.setColor(Color.CYAN);
                    drawUnderMouse(g, String.valueOf(npc.getId()) + " [Anim: " + npc.getAnimation() + "]");
                    break;
                }
            }
        }
    }

    private void drawUnderMouse(Graphics g, String text) {
        AWTUtil.drawBoldedString(g, text, Mouse.getX(), Mouse.getY());
    }
}
