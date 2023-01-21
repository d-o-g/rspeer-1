package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.api.Varps;

import java.util.function.IntSupplier;

public interface Quest {

    boolean isMembers();

    boolean isStarted();

    boolean isComplete();

    enum Free implements Quest {

        BLACK_KNIGHTS_FORTRESS(() -> Varps.get(130), 4),
        COOKS_ASSISTANT(() -> Varps.get(29), 2),
        THE_CORSAIR_CURSE(() -> Varps.getBitValue(6071), 60),
        DEMON_SLAYER(() -> Varps.getBitValue(2561), 3),
        DORICS_QUEST(() -> Varps.get(31), 100),
        DRAGON_SLAYER(() -> Varps.get(176), 10),
        ERNEST_THE_CHICKEN(() -> Varps.get(32), 3),
        GOBLIN_DIPLOMACY(() -> Varps.getBitValue(2378), 6),
        IMP_CATCHER(() -> Varps.get(160), 2),
        THE_KNIGHTS_SWORD(() -> Varps.get(122), 7),
        MISTHALIN_MYSTERY(() -> Varps.getBitValue(3468), 135),
        PIRATES_TREASURE(() -> Varps.get(71), 4),
        PRINCE_ALI_RESCUE(() -> Varps.get(273), 110),
        THE_RESTLESS_GHOST(() -> Varps.get(107), 5),
        ROMEO_AND_JULIET(() -> Varps.get(144), 100),
        RUNE_MYSTERIES(() -> Varps.get(63), 6),
        SHEEP_SHEARER(() -> Varps.get(179), 21),
        VAMPIRE_SLAYER(() -> Varps.get(178), 3),
        WITCHS_POTION(() -> Varps.get(67), 3),
        SHIELD_OF_ARRAV(null, -1) {
            @Override
            public boolean isStarted() {
                return Varps.get(145) > 0 || Varps.get(146) > 0;
            }

            @Override
            public boolean isComplete() {
                return Varps.get(145) == 7 || Varps.get(146) == 4;
            }
        };

        private final IntSupplier valueSupplier;
        private final int completionValue;

        Free(IntSupplier valueSupplier, int completionValue) {
            this.valueSupplier = valueSupplier;
            this.completionValue = completionValue;
        }

        @Override
        public boolean isMembers() {
            return false;
        }

        @Override
        public boolean isStarted() {
            return valueSupplier.getAsInt() > 0;
        }

        @Override
        public boolean isComplete() {
            return valueSupplier.getAsInt() == completionValue;
        }
    }

    enum Miniquest implements Quest {

        ARCHITECTURAL_ALLIANCE(() -> Varps.getBitValue(4982), 3),
        BEAR_YOUR_SOUL(() -> Varps.getBitValue(5078), 3),
        CURSE_OF_THE_EMPTY_LORD(() -> Varps.getBitValue(821), 1),
        ENCHANTED_KEY(() -> Varps.getBitValue(1391), 2047),
        ENTER_THE_ABYSS(() -> Varps.get(492), 4),
        FAMILY_PEST(() -> Varps.getBitValue(5347), 3),
        THE_GENERALS_SHADOW(() -> Varps.getBitValue(3330), 30),
        LAIR_OF_TARN_RAZORLOR(() -> Varps.getBitValue(3290), 3),
        THE_MAGE_ARENA(() -> Varps.get(267), 8),
        THE_MAGE_ARENA_II(() -> Varps.getBitValue(6067), 4),
        SKIPPY_AND_THE_MOGRES(() -> Varps.getBitValue(1344), 3),
        ALFRED_GRIMHANDS_BARCRAWL(null, -1) {
            @Override
            public boolean isStarted() {
                return Varps.get(77) >= 0 && Varps.get(76) > 0;
            }

            @Override
            public boolean isComplete() {
                return Varps.get(77) == 2 && Varps.get(76) >= 6;
            }
        };

        private final IntSupplier valueSupplier;
        private final int completionValue;

        Miniquest(IntSupplier valueSupplier, int completionValue) {
            this.valueSupplier = valueSupplier;
            this.completionValue = completionValue;
        }

        @Override
        public boolean isMembers() {
            return true;
        }

        @Override
        public boolean isStarted() {
            return valueSupplier.getAsInt() > 0;
        }

        @Override
        public boolean isComplete() {
            return valueSupplier.getAsInt() == completionValue;
        }
    }
}
