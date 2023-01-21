package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.Varpbit;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.providers.RSClientPreferences;

/**
 * Created by Spencer on 03/02/2018.
 */
public final class InterfaceOptions {

    private static final Varpbit VARPBIT_ORBS_DISABLED;
    private static final Varpbit VARPBIT_ACCEPT_AID;
    private static final Varpbit VARPBIT_REMAINING_XP;
    private static final Varpbit VARPBIT_LLNTD; //login logout notification timeout disabled...
    private static final Varpbit VARPBIT_MOUSE_CAMERA_DISABLED; //login logout notification timeout disabled...
    private static final int VARP_BRIGHTNESS = 166;
    private static final int VARP_MUSIC_VOLUME = 168;
    private static final int VARP_SOUND_EFFECT_VOLUME = 169;
    private static final int VARP_AREA_SOUND_VOLUME = 872;
    private static final int VARP_CHAT_EFFECTS_DISABLED = 171;
    private static final int VARP_SPLIT_PRIVATE_CHAT_ENABLED = 287;
    private static final int VARP_PROFANITY_FILTER_DISABLED = 1074;
    private static final int VARP_MOUSE_BUTTONS_1 = 170;
    private static final int VARP_ATTACK_OPTION_PRIORITY = 1074;

    //All are bits of varp 1055
    private static final Varpbit STONES_ARRANGEMENT;
    private static final Varpbit CHATBOX_MODE;
    private static final Varpbit SIDE_PANEL_MODE;
    private static final Varpbit SOCIAL_LIST_MODE;

    static {
        VARPBIT_ORBS_DISABLED = Varps.getBit(4084);
        VARPBIT_ACCEPT_AID = Varps.getBit(4180);
        VARPBIT_REMAINING_XP = Varps.getBit(4181);
        VARPBIT_LLNTD = Varps.getBit(1627);
        VARPBIT_MOUSE_CAMERA_DISABLED = Varps.getBit(4134);

        STONES_ARRANGEMENT = Varps.getBit(4607);
        CHATBOX_MODE = Varps.getBit(4608);
        SIDE_PANEL_MODE = Varps.getBit(4609);
        SOCIAL_LIST_MODE = Varps.getBit(6516);
    }

    private InterfaceOptions() {
        throw new IllegalAccessError();
    }

    public static TabLayout getTabLayout() {
        return TabLayout.values()[STONES_ARRANGEMENT.getValue()];
    }

    public static ChatboxMode getChatboxMode() {
        return ChatboxMode.values()[CHATBOX_MODE.getValue()];
    }

    public static SidePanelMode getSidePanelMode() {
        return SidePanelMode.values()[SIDE_PANEL_MODE.getValue()];
    }

    public static SocialTabMode getSocialTabMode() {
        return SocialTabMode.values()[SOCIAL_LIST_MODE.getValue()];
    }

    public static QuestTabMode getQuestTabMode() {
        int value = Varps.getBitValue(8168);
        for (QuestTabMode mode : QuestTabMode.values()) {
            if (value == mode.ordinal()) {
                return mode;
            }
        }
        throw new UnsupportedOperationException("Unsupported quest tab: " + value + "!");
    }

    public static ViewMode getViewMode() {
        if (Game.getClientPreferences().getResizable() != 1) {
            return ViewMode.RESIZABLE_MODE;
        }
        return ViewMode.FIXED_MODE;
    }

    public static boolean isAcceptingAid() {
        return VARPBIT_ACCEPT_AID.booleanValue();
    }

    public enum ViewMode {

        FIXED_MODE(4),
        RESIZABLE_MODE(0),
        UNDETERMINED(-1);

        private final int state;

        ViewMode(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    public enum TabLayout {
        BOX, LINE
    }

    public enum ChatboxMode {
        OPAQUE, TRANSPARENT
    }

    public enum SidePanelMode {
        TRANSPARENT, OPAQUE
    }

    public enum SocialTabMode {
        FRIENDS_LIST, IGNORE_LIST
    }

    public enum QuestTabMode {
        QUEST_LIST, ACHIEVEMENT_DIARIES, MINIGAMES, KOUREND_FAVOUR
    }

    public static class Controls {

        public static int getMouseButtons() {
            return Varps.getBoolean(VARP_MOUSE_BUTTONS_1) ? 1 : 2;
        }

        public static boolean isMouseCameraEnabled() {
            return !VARPBIT_MOUSE_CAMERA_DISABLED.booleanValue();
        }
/*
        public static AttackOptionPriority getAttackOptionPriority() {
            return AttackOptionPriority.values()[Varps.get(VARP_ATTACK_OPTION_PRIORITY)];
        }

        public enum AttackOptionPriority {
            // In client-respected order, don't change...
            COMBAT_LEVEL, // Depends on combat levels.
            LEFT_CLICK, // Left-click where available.
            RIGHT_CLICK // Always right-click.
        }*/
    }

    public static class Chat {

        public static boolean isChatEffectsEnabled() {
            return !Varps.getBoolean(VARP_CHAT_EFFECTS_DISABLED);
        }

        public static boolean isSplitPrivateChatEnabled() {
            return Varps.getBoolean(VARP_SPLIT_PRIVATE_CHAT_ENABLED);
        }

        public static boolean isProfanityFilterEnabled() {
            return !Varps.getBoolean(VARP_PROFANITY_FILTER_DISABLED);
        }

        public static boolean isLoginLogoutNotificationTimeoutEnabled() { //TODO holy name
            return !VARPBIT_LLNTD.booleanValue();
        }
    }

    public static class Audio {

        public static int getMusicVolume() {
            return Varps.get(VARP_MUSIC_VOLUME);
        }

        public static int getSoundEffectVolume() {
            return Varps.get(VARP_SOUND_EFFECT_VOLUME);
        }

        public static int getAreaSoundVolume() {
            return Varps.get(VARP_AREA_SOUND_VOLUME);
        }
    }

    public static class Display {

        public static boolean isRoofsHidden() {
            RSClientPreferences prefs = Game.getClient().getPreferences();
            return prefs != null && prefs.isRoofsHidden();
        }

        public static boolean isOrbsEnabled() {
            return !VARPBIT_ORBS_DISABLED.booleanValue();
        }

        public static int getBrightness() {
            return Varps.get(VARP_BRIGHTNESS);
        }

        public static boolean isRemainingXpOn() {
            return VARPBIT_REMAINING_XP.booleanValue();
        }
    }
}
