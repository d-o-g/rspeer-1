package org.rspeer.runetek.api.movement.transportation;

import org.rspeer.runetek.adapter.Varpbit;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;

public final class FairyRing {

    private static final int ROOT = InterfaceComposite.FAIRY_RING.getGroup();
    private static final int[][] TURN_INDICES = {{19, 20}, {21, 22}, {23, 24}};

    private static final InterfaceAddress CONFIRM_ADDRESS = new InterfaceAddress(ROOT, 26);

    private static final int[] VARPBITS = {3985, 3986, 3987};
    private static final char[][] CODES = {{'a', 'd', 'c', 'b'}, {'i', 'l', 'k', 'j'}, {'p', 's', 'r', 'q'}};

    private FairyRing() {
        throw new IllegalAccessError();
    }

    /**
     * Gets the nearest fairy ring destination
     *
     * @return the nearest fairy ring destination
     */
    public static Destination getNearest() {
        Destination best = null;
        double minDist = Double.MAX_VALUE;
        for (Destination destination : Destination.values()) {
            double currDist = destination.getPosition().distance();
            if (best == null || currDist < minDist) {
                best = destination;
                minDist = currDist;
            }
        }
        return best;
    }

    public static boolean zanaris() {
        SceneObject ring = SceneObjects.getNearest("Fairy ring");
        return ring.interact("Zanaris");
    }

    /**
     * Finds a nearby fairy ring and opens the configure menu
     *
     * @return true if the configure menu is opened
     */
    public static boolean open() {
        if (isInterfaceOpen()) {
            return true;
        }
        SceneObject ring = SceneObjects.getNearest("Fairy ring");
        return ring != null && ring.interact("Configure");
    }

    /**
     * Checks if the fairy ring input menu is opened
     *
     * @return whether or not the input menu is opened
     */
    public static boolean isInterfaceOpen() {
        return Interfaces.validateComponent(ROOT, 0);
    }

    /**
     * Presses the confirm button on the fairy ring configure interface
     *
     * @return whether or not the interact went through
     */
    public static boolean confirm() {
        InterfaceComponent confirm = Interfaces.lookup(CONFIRM_ADDRESS);
        return confirm != null && confirm.interact("Confirm");
    }

    /**
     * Enters in the given code and presses the confirm button to travel to the destination
     *
     * @param code the code to enter
     * @return whether or not we traveled
     */
    public static boolean travel(Destination code) {
        if (enterCode(code) && Time.sleepUntil(() -> getCurrentCode().equals(code.getCode()), 1200)) {
            Time.sleep(800, 1200);
            return confirm();
        }

        return false;
    }

    /**
     * Gets the current code using the varp values
     *
     * @return the current code
     */
    private static String getCurrentCode() {
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < VARPBITS.length; i++) {
            codeBuilder.append(getCharAt(i));
        }
        return codeBuilder.toString();
    }

    /**
     * Gets the current code using the varp values
     *
     * @return the current code
     */
    public static Destination getCurrentDestination() {
        return Destination.fromCode(getCurrentCode());
    }

    /**
     * Will enter the given code into the fairy ring input menu
     *
     * @param destination the code to enter
     * @return Whether the code was successfully entered
     */
    public static boolean enterCode(Destination destination) {
        char[] chars = destination.getCode().toCharArray();
        if (chars.length != 3) {
            return false;
        }

        boolean result = true;
        for (int i = 0; i < chars.length; i++) {
            char current = getCharAt(i);
            char dest = chars[i];

            if (current != dest) {
                result &= setCharTo(i, dest);
            }
        }

        return result && getCurrentDestination() == destination;
    }

    private static char getCharAt(int index) {
        int value = getVarpbitValue(index);
        if (value == -1) {
            return 0;
        }
        return CODES[index][value];
    }

    private static int getVarpbitValue(int index) {
        Varpbit bit = Varps.getBit(VARPBITS[index]);
        if (bit != null) {
            return bit.getValue();
        }
        return -1;
    }

    private static boolean setCharTo(int index, char dest) {
        if (!isCharValid(dest)) {
            return false;
        }

        int from = getVarpbitValue(index);
        int to = indexOf(index, dest);
        int rightDistance = distance(from, to, Move.RIGHT);
        int leftDistance = distance(from, to, Move.LEFT);

        int distance = Math.min(rightDistance, leftDistance);
        Move move = distance == rightDistance ? Move.RIGHT : Move.LEFT;
        InterfaceComponent moveComponent = Interfaces.getComponent(ROOT, TURN_INDICES[index][move.index]);

        if (moveComponent == null) {
            return false;
        }

        boolean result = true;
        for (int i = 0; i < distance; i++) {
            int value = getVarpbitValue(index);
            result &= moveComponent.interact(move.action)
                    && Time.sleepUntil(() -> getVarpbitValue(index) != value, 1200);
            Time.sleep(150, 250);
        }
        return result && Time.sleepUntil(() -> getCharAt(index) == dest, 1200);
    }

    private static int distance(int from, int to, Move move) {
        int distance = 0;
        while (from != to) {
            from += (move == Move.RIGHT ? 1 : -1);
            from = Math.floorMod(from, 4);
            distance++;
        }
        return distance;
    }

    private static int indexOf(int rotator, char c) {
        for (int i = 0; i < CODES[rotator].length; i++) {
            if (c == CODES[rotator][i]) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isCharValid(char check) {
        for (char[] chars : CODES) {
            for (char ch : chars) {
                if (check == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    private enum Move {

        LEFT(1, "Rotate counter-clockwise"),
        RIGHT(0, "Rotate clockwise");

        private final int index;
        private final String action;

        Move(int index, String action) {
            this.index = index;
            this.action = action;
        }
    }

    public enum Destination {

        AIQ("Mudskipper Point", new Position(2996, 3114, 0), "aiq"),
        AIR("(Island) South-east of Ardougne", new Position(2700, 3247, 0), "air"),
        AJQ("Cave south of Dorgesh-Kaan", new Position(2700, 3247, 0), "ajq"),
        AJR("Slayer cave", new Position(2780, 3613, 0), "ajr"),
        AJS("Penguins near Miscellania", new Position(2500, 3896, 0), "ajs"),
        AKQ("Piscatoris Hunter area", new Position(2319, 3619, 0), "akq"),
        AKS("Feldip Hunter area", new Position(2571, 2956, 0), "aks"),
        ALP("(Island) Lighthouse", new Position(2503, 3636, 0), "alp"),
        ALQ("Haunted Woods east of Canifis", new Position(3597, 3495, 0), "alq"),
        ALR("Abyssal Area", new Position(3059, 4875, 0), "alr"),
        ALS("McGrubor's Wood", new Position(2644, 3495, 0), "als"),

        BIP("(Island) South-west of Mort Myre", new Position(3410, 3324, 0), "bip"),
        BIQ("Kalphite Hive", new Position(3251, 3095, 0), "biq"),
        BIS("Ardougne Zoo - Unicorns", new Position(2635, 3266, 0), "bis"),
        BJR("Realm of the Fisher King", new Position(2650, 4730, 0), "bjr"),
        BJS("(Island) Near Zul-Andra", new Position(2150, 3070, 0), "bjs"),
        BKP("South of Castle Wars", new Position(2385, 3035, 0), "bkp"),
        BKQ("Enchanted Valley", new Position(3041, 4532, 0), "bkq"),
        BKR("Mort Myre Swamp, south of Canifis", new Position(3469, 3431, 0), "bkr"),
        BKS("Zanaris", new Position(2412, 4434, 0), "bks"),
        BLP("TzHaar area", new Position(2437, 5126, 0), "blp"),
        BLR("Legends' Guild", new Position(2740, 3351, 0), "blr"),

        CIP("(Island) Miscellania", new Position(2513, 3884, 0), "cip"),
        CIQ("North-west of Yanille", new Position(2528, 3127, 0), "ciq"),
        CIS("North of the Arceuus House Library", new Position(1639, 3868, 0), "cis"),
        CIR("North-east of the Farming Guild", new Position(1302, 3762, 0), "cir"),
        CJR("Sinclair Mansion (east)", new Position(2705, 3576, 0), "cjr"),
        CKP("Cosmic entity's plane", new Position(2075, 4848, 0), "ckp"),
        CKR("South of Tai Bwo Wannai Village", new Position(2801, 3003, 0), "ckr"),
        CKS("Canifis", new Position(3447, 3470, 0), "cks"),
        CLP("(Island) South of Draynor Village", new Position(3082, 3206, 0), "clp"),
        CLR("(Island) Ape Atoll", new Position(2740, 2738, 0), "clr"),
        CLS("(Island) Hazelmere's home", new Position(2682, 3081, 0), "cls"),

        DIP("(Sire Boss) Abyssal Nexus", new Position(3037, 4763, 0), "dip"),
        DIR("Gorak's Plane", new Position(1455, 3658, 0), "dir"),
        DIQ("Player-owned house", new Position(8035, 9899, 0), "diq"),
        DIS("Wizards' Tower", new Position(3108, 3149, 0), "dis"),
        DJP("Tower of Life", new Position(2658, 3230, 0), "djp"),
        DJR("Chasm of Fire", new Position(1455, 3658, 0), "djr"),
        DKP("South of Musa Point", new Position(2900, 3111, 0), "dkp"),
        DKR("Edgeville, Grand Exchange", new Position(3129, 3496, 0), "dkr"),
        DKS("Polar Hunter area", new Position(2744, 3719, 0), "dks"),
        DLQ("North of Nardah", new Position(3423, 3016, 0), "dlq"),
        DLR("(Island) Poison Waste south of Isafdar", new Position(2213, 3099, 0), "dlr"),
        DLS("Myreque hideout under The Hollows", new Position(3501, 9821, 3), "dls");

        private final String description;
        private final Position position;
        private final String code;

        Destination(String description, Position position, String code) {
            this.description = description;
            this.position = position;
            this.code = code;
        }

        public static Destination fromCode(String code) {
            for (Destination dest : Destination.values()) {
                if (dest.getCode().equalsIgnoreCase(code)) {
                    return dest;
                }
            }
            return null;
        }

        public String getDescription() {
            return description;
        }

        public Position getPosition() {
            return position;
        }

        public String getCode() {
            return code;
        }
    }
}