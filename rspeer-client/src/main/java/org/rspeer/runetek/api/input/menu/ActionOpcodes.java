package org.rspeer.runetek.api.input.menu;

import org.rspeer.runetek.adapter.scene.*;
import org.rspeer.runetek.api.scene.*;

public interface ActionOpcodes {

    int ITEM_ON_OBJECT = 1; // Using a 'Selected Item' on a SceneObject
    int SPELL_ON_OBJECT = 2; // Using a 'Selected Spell' on a SceneObject

    // Note: Interactable Objects can only have 5 defined actions (Any more the op == 0)
    // Note: The 4th defined action of an object has a abnormal op.
    // Note: All Interactable objects are examinable
    // Note: Whenever any of these actions are available, the action of EXAMINE_OBJECT is always present.
    //
    // Selecting the n'th index of action of a SceneObject; relative to its defined action array within its definition.
    int OBJECT_ACTION_0 = 3;    // ^ Index 0
    int OBJECT_ACTION_1 = 4;    // ^ Index 1
    int OBJECT_ACTION_2 = 5;    // ^ Index 2
    int OBJECT_ACTION_3 = 6;    // ^ Index 3
    int OBJECT_ACTION_4 = 1001; // ^ Index 4
    int EXAMINE_OBJECT = 1002;

    int ITEM_ON_NPC = 7;  // Using a 'Selected Item' on a NPC
    int SPELL_ON_NPC = 8;  // Using a 'Selected Spell' on a NPC

    // Note: NPCs can only have 5 defined actions.
    // Note: Whenever any of these actions are available, the action of EXAMINE_NPC is always present.
    // Note: If a the action is "Attack" the natural opcode is incremented by 2000 internally
    // Note: All NPCs are examinable
    //
    // Selecting the n'th index of action of a NPC; relative to its defined action array within its definition.
    int NPC_ACTION_0 = 9;   // ^ Index 0
    int NPC_ACTION_1 = 10;  // ^ Index 1
    int NPC_ACTION_2 = 11;  // ^ Index 2
    int NPC_ACTION_3 = 12;  // ^ Index 3
    int NPC_ACTION_4 = 13;  // ^ Index 4
    int EXAMINE_NPC = 1003;

    int ITEM_ON_PLAYER = 14;  // Using a 'Selected Item' on a Player
    int SPELL_ON_PLAYER = 15;  // Using a 'Selected Spell' on a Player


    // Note: Player actions are dynamic, meaning they can change, and are not final.
    // Note: All players share the same actions, excluding interaction with the local player.
    // See: RSClient.getPlayerActions(), these are defined for all players in the region
    // Note: There can be a maximum of 8 player actions.
    // Note: Players are not examinable (Unlike all other entitys).
    //
    // Selecting the n'th index of action of a Player; relative to the current player actions.
    int PLAYER_ACTION_0 = 44; // ^ Index 0
    int PLAYER_ACTION_1 = 45; // ^ Index 1
    int PLAYER_ACTION_2 = 46; // ^ Index 2
    int PLAYER_ACTION_3 = 47; // ^ Index 3
    int PLAYER_ACTION_4 = 48; // ^ Index 4
    int PLAYER_ACTION_5 = 49; // ^ Index 5
    int PLAYER_ACTION_6 = 50; // ^ Index 6
    int PLAYER_ACTION_7 = 51; // ^ Index 7

    @Deprecated
    int ITEM_ON_GROUND_ITEM = 16; // Using a 'Selected Item' on a Pickable

    @Deprecated
    int SPELL_ON_GROUND_ITEM = 17; // Using a 'Selected Spell' on a Pickable

    int ITEM_ON_PICKABLE = 16; // Using a 'Selected Item' on a Pickable
    int SPELL_ON_PICKABLE = 17; // Using a 'Selected Spell' on a Pickable

    //Note: The 2nd index action, if null, is defaulted to "Take"
    //Note: If any of the following actions are present, you can "Examine" the ground item.
    @Deprecated
    int GROUND_ITEM_ACTION_0 = 18;

    @Deprecated
    int GROUND_ITEM_ACTION_1 = 19;

    @Deprecated
    int GROUND_ITEM_ACTION_2 = 20;

    @Deprecated
    int GROUND_ITEM_ACTION_3 = 21;

    @Deprecated
    int GROUND_ITEM_ACTION_4 = 22;

    @Deprecated
    int EXAMINE_GROUND_ITEM = 1004;


    int PICKABLE_ACTION_0 = 18;
    int PICKABLE_ACTION_1 = 19;
    int PICKABLE_ACTION_2 = 20;
    int PICKABLE_ACTION_3 = 21;
    int PICKABLE_ACTION_4 = 22;
    int EXAMINE_PICKABLE = 1004;

    int WALK_HERE = 23;

    // All of these are for components with button types (See: component.getButtontType())
    int BUTTON_INPUT = 24; // Type 1
    int BUTTON_SELECTABLE_SPELL = 25; // Type 2
    int BUTTON_CLOSE = 26; // Type 3
    //--------- 27 does not exist
    int BUTTON_VAR_FLIP = 28; // Type 4
    int BUTTON_VAR_SET = 29; // Type 5
    int BUTTON_DIALOG = 30; // Type 6

    // Note: The following actions are ONLY available for table-type components (where component.getType() == 2)
    // Note: For all items (> 0 quantity) within the table the action "Examine" is always present,
    // if any of the following actions present.


    int ITEM_ON_ITEM = 31; // 'Selected Item'  -> Item
    int SPELL_ON_ITEM = 32; // 'Selected Spell' -> Item


    // Note: Item actions are not always enabled (Config Varp[30,30])
    // Note: If the 4'th action of the defined action of the corresponding
    // item within a slot is null, then it's defaulted to "Drop".
    int ITEM_ACTION_0 = 33;
    int ITEM_ACTION_1 = 34;
    int ITEM_ACTION_2 = 35;
    int ITEM_ACTION_3 = 36;
    int ITEM_ACTION_4 = 37;

    // Note: This action is only available if the owner of this
    // table has 'USABLE_ITEM' enabled. ( Config Varp[31,31] == 1 )
    int USE_ITEM = 38;


    // Note: These actions are extended by the table to all of its items,
    // meaning both item actions(if enabled) and the 'table actions'
    // are all possible actions for every item within the table.

    // Note: Table actions are always enabled/present(if any).
    //
    // Note: Tables, such as shops , usually disable the item defined actions (component.getActions())
    // Making its container actions (component.getTableActions()) the only implemented type.

    int TABLE_ACTION_0 = 39;
    int TABLE_ACTION_1 = 40;
    int TABLE_ACTION_2 = 41;
    int TABLE_ACTION_3 = 42;
    int TABLE_ACTION_4 = 43;


    int EXAMINE_ITEM = 1005;

    // Note: The following actions are ONLY for interfaces
    // who are 'interactable'.

    // Note: The index of action is placed into the first
    // argument of the action, and is incremented by one
    // for server-side purposes?
    //
    // Note: Actions can be disabled, via the config of the component:
    // action_enabled = (component.getConfig() >> 1 + action_index & 1) != 0). (See: InterfaceConfig)
    // though a action may be disabled when still enabled if the
    // component has a actionListener (its non-null).

    // Note: INTERFACE_ACTION is ONLY for actions between indexes [0, 4]

    int INTERFACE_ACTION = 57;


    int SPELL_ON_COMPONENT = 58; // 'Selected Spell' -> component

    // Note: INTERFACE_ACTION_2 is ONLY for actions between indexes [5, 9]
    // Note: This action is also used for components which open the menu on a left click
    // Example: Bank presets or fillers - left clicking them opens the menu instead of performing the action
    int INTERFACE_ACTION_2 = 1007;

    int CANCEL = 1006;

    static String verbose(int opcode, int primaryArg, int secondaryArg, int tertiaryArg) {
        opcode = prune(opcode);
        StringBuilder builder = new StringBuilder();
        switch (opcode) {
            case OBJECT_ACTION_0:
            case OBJECT_ACTION_1:
            case OBJECT_ACTION_2:
            case OBJECT_ACTION_3:
            case OBJECT_ACTION_4: {
                builder.append("ObjectAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("id=")
                        .append(primaryArg)
                        .append("|x=")
                        .append(secondaryArg + Scene.getBaseX())
                        .append("|y=")
                        .append(tertiaryArg + Scene.getBaseY())
                        .append("]");
                break;
            }

            case PICKABLE_ACTION_0:
            case PICKABLE_ACTION_1:
            case PICKABLE_ACTION_2:
            case PICKABLE_ACTION_3:
            case PICKABLE_ACTION_4: {
                builder.append("PickableAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("id=")
                        .append(primaryArg)
                        .append("|x=")
                        .append(secondaryArg + Scene.getBaseX())
                        .append("|y=")
                        .append(tertiaryArg + Scene.getBaseY())
                        .append("]");
                break;
            }

            case NPC_ACTION_0:
            case NPC_ACTION_1:
            case NPC_ACTION_2:
            case NPC_ACTION_3:
            case NPC_ACTION_4:
            case PLAYER_ACTION_0:
            case PLAYER_ACTION_1:
            case PLAYER_ACTION_2:
            case PLAYER_ACTION_3:
            case PLAYER_ACTION_4:
            case PLAYER_ACTION_5:
            case PLAYER_ACTION_6:
            case PLAYER_ACTION_7: {
                PathingEntity entity;
                if (opcode >= PLAYER_ACTION_0) {
                    entity = Players.getAt(primaryArg);
                } else {
                    entity = Npcs.getAt(primaryArg);
                }
                builder.append("PathingEntityAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("index=")
                        .append(primaryArg);
                if (entity != null) {
                    builder.append("|name=").append(entity.getName());
                    if (entity instanceof Npc) {
                        builder.append("|id=").append(entity.getId());
                    }
                    builder.append("|x=").append(entity.getX()).append("|y=").append(entity.getY());
                }
                builder.append("]");
                break;
            }

            case BUTTON_INPUT:
            case BUTTON_SELECTABLE_SPELL:
            case BUTTON_CLOSE:
            case BUTTON_VAR_FLIP:
            case BUTTON_DIALOG: {
                builder.append("ButtonAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("uid=")
                        .append(tertiaryArg)
                        .append("|group=")
                        .append(tertiaryArg >> 16)
                        .append("|component=")
                        .append(tertiaryArg & 0xffff);
                if (secondaryArg != -1) {
                    builder.append("|subComponent=")
                            .append(secondaryArg);
                }
                builder.append("]");
                break;
            }

            case TABLE_ACTION_0:
            case TABLE_ACTION_1:
            case TABLE_ACTION_2:
            case TABLE_ACTION_3:
            case TABLE_ACTION_4:
            case ITEM_ACTION_0:
            case ITEM_ACTION_1:
            case ITEM_ACTION_2:
            case ITEM_ACTION_3:
            case ITEM_ACTION_4: {
                builder.append("ItemTableAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("itemId=")
                        .append(primaryArg)
                        .append("|itemIndex=")
                        .append(secondaryArg)
                        .append("|componentUid=")
                        .append(tertiaryArg)
                        .append("|componentGroup=")
                        .append(tertiaryArg >> 16)
                        .append("|componentIndex=")
                        .append(tertiaryArg & 0xffff)
                        .append("]");
                break;
            }

            case INTERFACE_ACTION:
            case INTERFACE_ACTION_2: {
                builder.append("ComponentAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("actionIndex=")
                        .append(primaryArg);
                if (secondaryArg != -1) {
                    builder.append("|subComponent=")
                            .append(secondaryArg);
                }
                builder.append("|componentUid=")
                        .append(tertiaryArg)
                        .append("|componentGroup=")
                        .append(tertiaryArg >> 16)
                        .append("|componentIndex=")
                        .append(tertiaryArg & 0xffff)
                        .append("]");
                break;
            }

            case ITEM_ON_OBJECT:
            case ITEM_ON_NPC:
            case ITEM_ON_PLAYER:
            case ITEM_ON_PICKABLE: {
                builder.append("ItemOnEntityAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("index=")
                        .append(primaryArg)
                        .append("|x=")
                        .append(secondaryArg + Scene.getBaseX())
                        .append("|y=")
                        .append(tertiaryArg + Scene.getBaseY())
                        .append("]");
                break;
            }

            case ITEM_ON_ITEM: {
                builder.append("ItemOnItemAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("itemId=")
                        .append(primaryArg)
                        .append("|itemIndex=")
                        .append(secondaryArg)
                        .append("|componentUid=")
                        .append(tertiaryArg)
                        .append("]");
                break;
            }

            case SPELL_ON_OBJECT:
            case SPELL_ON_NPC:
            case SPELL_ON_PLAYER:
            case SPELL_ON_PICKABLE: {
                builder.append("SpellOnEntityAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("index=")
                        .append(primaryArg)
                        .append("|x=")
                        .append(secondaryArg + Scene.getBaseX())
                        .append("|y=")
                        .append(tertiaryArg + Scene.getBaseY())
                        .append("]");
                break;
            }

            case SPELL_ON_ITEM: {
                builder.append("SpellOnItemAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("itemId=")
                        .append(primaryArg)
                        .append("|itemIndex=")
                        .append(secondaryArg)
                        .append("|componentUid=")
                        .append(tertiaryArg)
                        .append("]");
                break;
            }

            case SPELL_ON_COMPONENT: {
                builder.append("SpellOnComponentAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[");
                if (secondaryArg != -1) {
                    builder.append("subComponent=")
                            .append(secondaryArg)
                            .append("|");
                }
                builder.append("componentUid=")
                        .append(tertiaryArg)
                        .append("|groupIndex=")
                        .append(tertiaryArg >> 16)
                        .append("|componentIndex=")
                        .append(tertiaryArg & 0xffff)
                        .append("]");
                break;
            }

            case USE_ITEM: {
                builder.append("UseItemAction<")
                        .append(ContextMenu.getOpcodeName(opcode))
                        .append(">[")
                        .append("itemId=")
                        .append(primaryArg)
                        .append("|itemIndex=")
                        .append(secondaryArg)
                        .append("|componentUid=")
                        .append(tertiaryArg)
                        .append("|groupIndex=")
                        .append(tertiaryArg >> 16)
                        .append("|componentIndex=")
                        .append(tertiaryArg & 0xffff)
                        .append("]");
                break;
            }
        }
        return builder.toString();
    }

    static int prune(int opcode) {
        return opcode >= 2000 ? opcode - 2000 : opcode;
    }
}
