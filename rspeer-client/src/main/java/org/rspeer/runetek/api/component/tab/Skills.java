package org.rspeer.runetek.api.component.tab;

import org.rspeer.runetek.api.Game;

/**
 * Created by Spencer on 31/01/2018.
 */
public final class Skills {

    public static final int MAX_LEVEL = 99;
    public static final int MAX_VIRTUAL_LEVEL = 126;
    public static final int MAX_EXPERIENCE = 200_000_000;

    public static final int[] XP_TABLE;

    private Skills() {
        throw new IllegalAccessError();
    }

    static {
        XP_TABLE = new int[127];
        XP_TABLE[0] = 0;
        for (int level = 1; level < XP_TABLE.length; level++) {
            XP_TABLE[level] = calculateExperienceAt(level);
        }
    }

    /**
     * @param skill The skill
     * @return The base level of the skill
     */
    public static int getLevel(Skill skill) {
        return Game.getClient().getLevels()[skill.ordinal()];
    }

    /**
     * @param skill The skill
     * @return The current level. This takes into account boosted or drained stats
     */
    public static int getCurrentLevel(Skill skill) {
        return Game.getClient().getCurrentLevels()[skill.ordinal()];
    }

    /**
     * @param skill The skill
     * @return The experience in the skill
     */
    public static int getExperience(Skill skill) {
        return Game.getClient().getExperiences()[skill.ordinal()];
    }

    /**
     * @param skill the skill
     * @return the experience needed for the next level
     */
    public static int getExperienceToNextLevel(Skill skill) {
        int nextLvl = getVirtualLevel(skill) + 1;
        if (nextLvl > MAX_VIRTUAL_LEVEL) {
            return MAX_EXPERIENCE - getExperience(skill);
        }
        return getExperienceAt(nextLvl) - getExperience(skill);
    }

    public static int getVirtualLevel(Skill skill) {
        int xp = getExperience(skill);
        for (int i = XP_TABLE.length - 1; i > 0; i--) {
            int xpat = XP_TABLE[i];
            if (xp >= xpat) {
                return i;
            }
        }
        return -1;
    }

    public static int getLevelAt(int xp) {
        for (int i = XP_TABLE.length - 1; i > 0; i--) {
            if (i <= MAX_LEVEL) {
                int xpat = XP_TABLE[i];
                if (xp >= xpat) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @param deslvl The destination level
     * @return The experience needed to progress to the desired level.
     */
    private static int calculateExperienceAt(int deslvl) {
        double t = 0;
        for (int lvl = 1; lvl < deslvl; lvl++) {
            t += Math.floor(lvl + 300 * Math.pow(2, lvl / 7.0));
        }
        return (int) Math.floor(t / 4);
    }

    /**
     * @param deslvl The destination level
     * @return The experience needed to progress to the desired level.
     */
    public static int getExperienceAt(int deslvl) {
        return deslvl >= 0 && deslvl < XP_TABLE.length ? XP_TABLE[deslvl] : 0;
    }

    public static int getTotalLevel() {
        int sum = 0;
        for (Skill skill : Skill.values()) {
            sum += Skills.getLevel(skill);
        }
        return sum;
    }
}
