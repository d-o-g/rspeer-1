package org.rspeer.runetek.providers;

public interface RSWorld extends RSProvider {

    int MEMBERS = 0x1;

    int PVP = 0x4;

    int BOUNTY = 0x20;

    int SKILL_TOTAL = 0x80;

    int HIGH_RISK = 0x400;

    int LMS = 0x4000;

    int TOURNAMENT = 0x2000000;

    int DEADMAN = 0x20000000;

    int SEASONAL_DEADMAN = 0x40000000;

    int LOCATION_US = 0;

    int LOCATION_UK = 1;

    int LOCATION_DE = 7;

    int LOCATION_AU = 3;

    int getId();

    int getLocation();

    int getMask();

    int getPopulation();

    String getActivity();

    String getDomain();

    default boolean isMembers() {
        return (getMask() & MEMBERS) == MEMBERS;
    }

    default boolean isPVP() {
        return (getMask() & PVP) == PVP;
    }

    default boolean isBounty() {
        return (getMask() & BOUNTY) == BOUNTY;
    }

    default boolean isHighRisk() {
        return (getMask() & HIGH_RISK) == HIGH_RISK;
    }

    default boolean isSkillTotal() {
        return (getMask() & SKILL_TOTAL) == SKILL_TOTAL;
    }

    default boolean isLastManStanding() {
        return (getMask() & LMS) == LMS;
    }

    default boolean isSeasonDeadman() {
        return (getMask() & SEASONAL_DEADMAN) == SEASONAL_DEADMAN;
    }

    default boolean isDeadman() {
        return isSeasonDeadman() || (getMask() & DEADMAN) == DEADMAN;
    }

    default boolean isTournament() {
        return (getMask() & TOURNAMENT) == TOURNAMENT;
    }

    default Locale getLocale() {
        int location = getLocation();
        for (Locale locale : Locale.values()) {
            if (locale.id == location) {
                return locale;
            }
        }
        throw new IllegalStateException("Unknown location: " + location + ". Please report!");
    }

    enum Locale {

        US(LOCATION_US),
        UK(LOCATION_UK),
        DE(LOCATION_DE),
        AU(LOCATION_AU);

        private final int id;

        Locale(int id) {
            this.id = id;
        }
    }
}