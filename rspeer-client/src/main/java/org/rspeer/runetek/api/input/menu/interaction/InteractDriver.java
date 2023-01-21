package org.rspeer.runetek.api.input.menu.interaction;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.InterfaceConfig;
import org.rspeer.runetek.api.input.Mouse;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.input.menu.ContextMenu;

import java.awt.*;
import java.util.function.Supplier;

public class InteractDriver {

    public static final InteractDriver INSTANCE = new InteractDriver();

    private static final Supplier<Point> CLICK_POINT_SUPPLIER = () -> new Point(Random.mid(1, 516), Random.mid(1, 338));
    private static final Point SESSION_POINT = CLICK_POINT_SUPPLIER.get();

    public static MenuAction getAction(Interactable interactable, String action) {
        if (interactable instanceof Npc) {
            Npc npc = (Npc) interactable;
            int opcode = getOpcode(interactable, action, ActionOpcodes.NPC_ACTION_0);
            if (action.equals("Cast") && Game.getClient().isSpellSelected()) {
                opcode = ActionOpcodes.SPELL_ON_NPC;
            } else if (action.equals("Examine")) {
                opcode = ActionOpcodes.EXAMINE_NPC;
            } else if (action.equals("Use") && Game.getClient().getItemSelectionState() != 0) {
                opcode = ActionOpcodes.ITEM_ON_NPC;
            }

            if (opcode != -1) {
                return new MenuAction(action, npc.getName(), opcode, npc.getIndex(), 0, 0);
            }
        }

        if (interactable instanceof Player) {
            Player player = (Player) interactable;
            int opcode = getOpcode(interactable, action, ActionOpcodes.PLAYER_ACTION_0);
            if (action.equals("Cast") && Game.getClient().isSpellSelected()) {
                opcode = ActionOpcodes.SPELL_ON_PLAYER;
            } else if (action.equals("Use") && Game.getClient().getItemSelectionState() != 0) {
                opcode = ActionOpcodes.ITEM_ON_PLAYER;
            }

            if (opcode != -1) {
                return new MenuAction(action, player.getName(), opcode, player.getIndex(), 0, 0);
            }
        }

        if (interactable instanceof SceneObject) {
            SceneObject obj = (SceneObject) interactable;
            int opcode = getOpcode(interactable, action, ActionOpcodes.OBJECT_ACTION_0);
            if (action.equals("Cast") && Game.getClient().isSpellSelected()) {
                opcode = ActionOpcodes.SPELL_ON_OBJECT;
            } else if (action.equals("Use") && Game.getClient().getItemSelectionState() != 0) {
                opcode = ActionOpcodes.ITEM_ON_OBJECT;
            } else if (action.equals("Examine")) {
                opcode = ActionOpcodes.EXAMINE_OBJECT;
            }

            if (opcode != -1) {
                return new MenuAction(action, obj.getName(), opcode, obj.getId(), obj.getSceneX(), obj.getSceneY());
            }
        }

        if (interactable instanceof InterfaceComponent) {
            InterfaceComponent c = (InterfaceComponent) interactable;

            if (c.getButtonType() == 1) {
                return new MenuAction(c.getProvider().getToolTip(), "", ActionOpcodes.BUTTON_INPUT, 0, -1, c.getUid());
            } else if ((c.getButtonType() == 2 || InterfaceConfig.getSpellTargets(c.getConfig()) != 0) && !Game.getClient().isSpellSelected()) {
                if (InterfaceConfig.getSpellTargets(c.getConfig()) == 0) {
                    action = null;
                } else if (c.getProvider().getSelectedAction() != null && c.getSelectedAction().trim().length() != 0) {
                    action = c.getSelectedAction();
                } else {
                    action = null;
                }
                if (action != null) {
                    return new MenuAction("Cast -> ", "", ActionOpcodes.BUTTON_SELECTABLE_SPELL, 0, -1, c.getUid());
                }
            } else if (c.getButtonType() == 3) {
                return new MenuAction("Close", "", ActionOpcodes.BUTTON_CLOSE, 0, -1, c.getUid());
            } else if (c.getButtonType() == 4) {
                return new MenuAction(c.getToolTip(), "", ActionOpcodes.BUTTON_VAR_FLIP, 0, -1, c.getUid());
            } else if (c.getButtonType() == 5) {
                return new MenuAction(c.getToolTip(), "", ActionOpcodes.BUTTON_VAR_SET, 0, -1, c.getUid());
            } else if (c.getButtonType() == 6 && Dialog.getPleaseWaitComponent() == null) {
                return new MenuAction(c.getToolTip(), "", ActionOpcodes.BUTTON_DIALOG, 0, c.getComponentIndex(), c.getUid());
            } else {
                int index = indexOf(c.getRawActions(), action);
                if (index != -1 && ++index >= 0) {
                    return new MenuAction(
                            action,
                            c.getText(),
                            index > 5 ? ActionOpcodes.INTERFACE_ACTION_2 : ActionOpcodes.INTERFACE_ACTION,
                            index,
                            c.getParentUid() == -1 ? -1 : c.getComponentIndex(),
                            c.getUid()
                    );
                }

                if (Game.getClient().isSpellSelected() && InterfaceConfig.allowsSpells(c.getConfig())) {
                    return new MenuAction(c.getSpellName(), c.getName(), ActionOpcodes.SPELL_ON_COMPONENT, 0, c.getComponentIndex(), c.getUid());
                }

                boolean var18;
                String[] actions = c.getRawActions();
                for (int var5 = 9; var5 >= 5; --var5) {
                    int var8 = c.getConfig();
                    var18 = (var8 >> var5 + 1 & 0x1) != 0;
                    String var4 = var18 || c.getMousePressListeners() != null
                            ? actions.length > var5 && actions[var5] != null && actions[var5].trim().length() != 0
                            ? actions[var5] : null : null;

                    if (var4 != null) {
                        return new MenuAction(var4, "", ActionOpcodes.INTERFACE_ACTION_2, var5 + 1, c.getComponentIndex(), c.getUid());
                    }
                }

                for (int var7 = 4; var7 >= 0; --var7) {
                    String var17 = method333(c, var7);
                    if (var17 != null) {
                        return new MenuAction(var17, "", ActionOpcodes.INTERFACE_ACTION, var7 + 1, c.getComponentIndex(), c.getUid());
                    }
                }

                int var8 = c.getConfig();
                var18 = (var8 & 1) != 0;
                if (var18) {
                    return new MenuAction("Continue", "", ActionOpcodes.BUTTON_DIALOG, 0, c.getComponentIndex(), c.getUid());
                }

                if (InterfaceConfig.getSpellTargets(c.getConfig()) == 0) {
                    action = null;
                } else if (c.getSelectedAction() != null && c.getSelectedAction().trim().length() != 0) {
                    action = c.getSelectedAction();
                } else {
                    action = null;
                }

                if (action != null) {
                    return new MenuAction(action, "", ActionOpcodes.BUTTON_SELECTABLE_SPELL, 0, c.getComponentIndex(), c.getUid());
                }
            }
        }

        if (interactable instanceof Pickable) {
            Pickable pickable = (Pickable) interactable;
            String[] actions = interactable.getRawActions();
            int opcode = getOpcode(interactable, action, ActionOpcodes.PICKABLE_ACTION_0);
            if (opcode == -1 && (actions == null || actions[2] == null || actions[2].equals("null")) && action.equalsIgnoreCase("Take")) {
                opcode = ActionOpcodes.PICKABLE_ACTION_2;
            } else if (action.equals("Cast") && Game.getClient().isSpellSelected()) {
                opcode = ActionOpcodes.SPELL_ON_PICKABLE;
            } else if (action.equals("Examine")) {
                opcode = ActionOpcodes.EXAMINE_PICKABLE;
            }
            return new MenuAction(action, "", opcode, pickable.getId(), pickable.getSceneX(), pickable.getSceneY());
        }

        if (interactable instanceof Item) {
            Item item = (Item) interactable;
            if (item.getComponent().getType() == 2) {
                int opcode = getOpcode(interactable, action, ActionOpcodes.ITEM_ACTION_0);
                if (action.equals("Cast")) {
                    opcode = ActionOpcodes.SPELL_ON_ITEM;
                } else if (action.equals("Use")) {
                    opcode = Game.getClient().getItemSelectionState() != 0 ? ActionOpcodes.ITEM_ON_ITEM : ActionOpcodes.USE_ITEM;
                } else if (action.equals("Drop") && opcode == -1) {
                    opcode = ActionOpcodes.ITEM_ACTION_4;
                }
                return new MenuAction(action, item.getName(), opcode, item.getId(), item.getIndex(), item.getInteractableComponent().getUid());
            }
            return getAction(item.getInteractableComponent(), action);
        }

        return null;
    }

    public static MenuAction getAction(Interactable interactable, int opcode) {
        return getAction(interactable, opcode, 0);
    }

    public static MenuAction getAction(Interactable interactable, int opcode, int actionIndex) {
        if (interactable instanceof Npc) {
            Npc npc = (Npc) interactable;
            return new MenuAction("", npc.getName(), opcode, npc.getIndex(), 0, 0);
        }

        if (interactable instanceof Player) {
            Player player = (Player) interactable;
            return new MenuAction("", player.getName(), opcode, player.getIndex(), 0, 0);
        }

        if (interactable instanceof SceneObject) {
            SceneObject obj = (SceneObject) interactable;
            return new MenuAction("", obj.getName(), opcode, obj.getId(), obj.getSceneX(), obj.getSceneY());
        }

        if (interactable instanceof Pickable) {
            Pickable pickable = (Pickable) interactable;
            return new MenuAction("", "", opcode, pickable.getId(), pickable.getSceneX(), pickable.getSceneY());
        }

        if (interactable instanceof InterfaceComponent) {
            InterfaceComponent c = (InterfaceComponent) interactable;

            if (c.getButtonType() == 1) {
                return new MenuAction(c.getProvider().getToolTip(), "", ActionOpcodes.BUTTON_INPUT, 0, -1, c.getUid());
            } else if ((c.getButtonType() == 2 || InterfaceConfig.getSpellTargets(c.getConfig()) != 0) && !Game.getClient().isSpellSelected()) {
                return new MenuAction("Cast -> ", "", ActionOpcodes.BUTTON_SELECTABLE_SPELL, 0, -1, c.getUid());
            } else if (c.getButtonType() == 3) {
                return new MenuAction("Close", "", ActionOpcodes.BUTTON_CLOSE, 0, -1, c.getUid());
            } else if (c.getButtonType() == 4) {
                return new MenuAction(c.getToolTip(), "", ActionOpcodes.BUTTON_VAR_FLIP, 0, -1, c.getUid());
            } else if (c.getButtonType() == 5) {
                return new MenuAction(c.getToolTip(), "", ActionOpcodes.BUTTON_VAR_SET, 0, -1, c.getUid());
            } else if (c.getButtonType() == 6 && Dialog.getPleaseWaitComponent() == null) {
                return new MenuAction(c.getToolTip(), "", ActionOpcodes.BUTTON_DIALOG, 0, c.getComponentIndex(), c.getUid());
            } else {
                return new MenuAction(
                        "",
                        c.getText(),
                        opcode,
                        actionIndex + 1,
                        c.getParentUid() == -1 ? -1 : c.getComponentIndex(),
                        c.getUid()
                );
            }
        }

        if (interactable instanceof Item) {
            Item item = (Item) interactable;
            if (item.getComponent().getType() == 2) {
                return new MenuAction("", item.getName(), opcode, item.getId(), item.getIndex(), item.getInteractableComponent().getUid());
            }
            return getAction(item.getInteractableComponent(), opcode, actionIndex);
        }

        return null;
    }

    private static String method333(InterfaceComponent var0, int var1) {
        int var2 = var0.getConfig();
        boolean var3 = (var2 >> var1 + 1 & 1) != 0;
        if (!var3 && var0.getMousePressListeners() == null) {
            return null;
        }
        String[] actions = var0.getRawActions();
        return actions.length > var1 && actions[var1] != null && actions[var1].trim().length() != 0 ? actions[var1] : null;
    }

    private static int getOpcode(Interactable interactable, String action, int opcodeOffset) {
        int i = indexOf(interactable.getRawActions(), action);
        if (opcodeOffset == ActionOpcodes.OBJECT_ACTION_0 && i == 4) {
            return ActionOpcodes.OBJECT_ACTION_4; //this is 1001 and object_action_0 is 3 so its special case
        }
        return i >= 0 ? (i + opcodeOffset) : -1;
    }

    private static int indexOf(String[] actions, String action) {
        if (action == null || actions == null) {
            return -1;
        }
        for (int i = 0; i < actions.length; i++) {
            String cmp = actions[i];
            if (action.equalsIgnoreCase(cmp)) {
                return i;
            }
        }
        return -1;
    }

    public boolean interact(Interactable interactable, int opcode, int actionIndex) {
        return interact(() -> getAction(interactable, opcode, actionIndex));
    }

    public boolean interact(Interactable interactable, int opcode) {
        return interact(() -> getAction(interactable, opcode));
    }

    public boolean interact(Interactable interactable, String actionText) {
        return interact(() -> getAction(interactable, actionText));
    }

    private boolean interact(Supplier<MenuAction> supplier) {
//        if (ContextMenu.isOpen()) {
//            ContextMenu.close();
//            return false;
//        }

        MenuAction action = supplier.get();
        if (action != null) {
            //set hovered uid
            Point point = CLICK_POINT_SUPPLIER.get();
            Game.getClient().setForcingInteraction(true);
            Game.getClient().setForcedAction(action);
            /*Game.getClient().processAction(action.getSecondaryArg(),
                    action.getTertiaryArg(), action.getOpcode(),
                    action.getPrimaryArg(), action.getAction(),
                    action.getTarget(), 0, 0);*/
            Mouse.click(point.x, point.y);
            return true;
        }
        return false;
    }
}
