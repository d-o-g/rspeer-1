package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.providers.RSInterfaceComponent;

import java.awt.event.KeyEvent;
import java.util.function.Predicate;

/**
 * @author Jasper
 * <p>
 * Dialog class is used for any dialog interactions inside Runescape. These methods
 * used to reside inside Interfaces but to prevent clutter they have been moved to Dialog.
 */
public final class Dialog {

    private static final InterfaceAddress CHAT_OPTIONS = new InterfaceAddress(() ->
            Interfaces.getFirst(InterfaceComposite.CHAT_OPTIONS.getGroup(),
                    comp -> comp.getComponentCount() > 1
            ));

    private Dialog() {
        throw new IllegalAccessError();
    }

    /**
     * @return {@code true} if the please wait component (usually appears after clicking a dialog option) is visible
     */
    public static boolean isProcessing() {
        return Game.getClient().getPleaseWaitComponent() != null;
    }

    /**
     * @return The please wait component (usually appears after clicking a dialog option)
     */
    public static InterfaceComponent getPleaseWaitComponent() {
        return Functions.mapOrNull(() -> Game.getClient().getPleaseWaitComponent(), RSInterfaceComponent::getWrapper);
    }

    /**
     * @return {@code null} if none are present, or one of {@link InterfaceComposite#PLAYER_DIALOG},
     * {@link InterfaceComposite#NPC_DIALOG} and {@link InterfaceComposite#LEVEL_UP_DIALOG}
     */
    static InterfaceComposite getOpenType() {
        if (Interfaces.isOpen(InterfaceComposite.PLAYER_DIALOG.getGroup())) {
            return InterfaceComposite.PLAYER_DIALOG;
        } else if (Interfaces.isOpen(InterfaceComposite.NPC_DIALOG.getGroup())) {
            return InterfaceComposite.NPC_DIALOG;
        } else if (Interfaces.isOpen(InterfaceComposite.LEVEL_UP_DIALOG.getGroup())) {
            return InterfaceComposite.LEVEL_UP_DIALOG;
        } else if (Interfaces.isOpen(InterfaceComposite.ITEM_DIALOG.getGroup())) {
            return InterfaceComposite.ITEM_DIALOG;
        } else if (Interfaces.isOpen(InterfaceComposite.PLAIN_DIALOG.getGroup())) {
            return InterfaceComposite.PLAIN_DIALOG;
        } else if (Interfaces.isOpen(InterfaceComposite.GUIDANCE_DIALOG.getGroup())) {
            return InterfaceComposite.GUIDANCE_DIALOG;
        }
        return null;
    }

    /**
     * @return {@code true} if dialog is open (not chat options). This can be
     * level up dialog, player or npc dialog
     */
    public static boolean isViewingChat() {
        return getOpenType() != null;
    }

    /**
     * @return {@code true} if chat or chat options are present
     */
    public static boolean isOpen() {
        return isViewingChatOptions() || isViewingChat();
    }

    /**
     * @return {@code true} if any chat options are visible
     */
    public static boolean isViewingChatOptions() {
        return Interfaces.isOpen(InterfaceComposite.CHAT_OPTIONS.getGroup());
    }

    /**
     * @return The click here to continue button
     */
    public static InterfaceComponent getContinue() {
        InterfaceComponent cmp = Interfaces.firstByText(x -> x.toLowerCase().contains("click here to continue"));
        return cmp != null
                && (InterfaceConfig.isDialogOption(cmp.getConfig()) || cmp.containsAction("Continue")
                || (cmp.getTextColor() == 128 && cmp.getFontId() == 496))
                ? cmp : null;
    }

    /**
     * @return {@code true} if the click here to continue button is visible
     */
    public static boolean canContinue() {
        return !isViewingChatOptions() && getContinue() != null;
    }

    /**
     * @return {@code true} if the continue button was successfully clicked
     */
    public static boolean processContinue() {
        InterfaceComponent cmp = getContinue();
        if (isProcessing()) {
            return true;
        } else if (cmp != null) {
            Keyboard.pressEventKey(KeyEvent.VK_SPACE);
            return Time.sleepUntil(Dialog::isProcessing, 600);
        }
        return false;
    }

    /**
     * @param chatOptionIndex The index of the chat option to select
     * @return {@code true} if the chat option was successfully processed
     */
    public static boolean process(int chatOptionIndex) {
        if (isProcessing()) {
            return true;
        } else if (canContinue()) {
            return processContinue();
        } else if (isViewingChatOptions()) {
            InterfaceComposite open = getOpenType();
            if (open == null) {
                Keyboard.sendKey((char) ('1' + chatOptionIndex));
                return Time.sleepUntil(Dialog::isProcessing, 600);
            }

            InterfaceComponent component = Interfaces.getComponent(open.getGroup(), 1);
            if (component != null) {
                component = component.getComponent(1 + chatOptionIndex);
                return component != null && component.interact(x -> true);
            }
        }
        return false;
    }


    /**
     * @param predicate the predicate that the chat option should match
     * @return {@code true} if an option was successfully interacted with or is already being interacted with
     */
    public static boolean process(Predicate<String> predicate) {
        if (isProcessing()) {
            return true;
        } else if (canContinue()) {
            return processContinue();
        }

        InterfaceComponent[] options = getChatOptions();
        for (int i = 0; i < options.length; i++) {
            InterfaceComponent option = options[i];
            if (predicate.test(option.getText())) {
                return process(i);
            }
        }
        return false;
    }

    /**
     * @param options Options to select
     * @return {@code true} if an option was successfully interacted with or is already being interacted with
     */
    public static boolean process(String... options) {
        if (isProcessing()) {
            return true;
        } else if (canContinue()) {
            return processContinue();
        }

        for (String option : options) {
            if (process(x -> x.toLowerCase().contains(option.toLowerCase()))) {
                return true;
            }
        }
        return false;
    }

    public static InterfaceComponent[] getChatOptions() {
        if (!isViewingChatOptions() || isProcessing()) {
            return new InterfaceComponent[0];
        }

        InterfaceComponent container = Interfaces.lookup(CHAT_OPTIONS);
        if (container == null) {
            return new InterfaceComponent[0];
        }

        return container.getComponents(x -> x.getConfig() != 0);
    }

    public static InterfaceComponent getChatOption(Predicate<String> predicate) {
        InterfaceComponent[] options = getChatOptions();
        for (InterfaceComponent option : options) {
            if (predicate.test(option.getText())) {
                return option;
            }
        }
        return null;
    }

}
