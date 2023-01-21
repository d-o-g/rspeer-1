package org.rspeer.networking.acuity.services.player_cache.domain;

import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaLocation;

import java.util.Map;

/**
 * Created by Zachary Herridge on 7/17/2018.
 */
public class CachedPlayer {

    private String ign;
    private String email;

    private int combatLevel;
    private int spellBook;
    private int world;
    private HpaLocation location;

    private Map<String, Integer> levels;
    private Map<Integer, Integer> inventory;
    private Map<Integer, Integer> equipment;
    private Map<Integer, Integer> bank;
    private Map<Integer, Integer> questProgress;
    private Map<String, Integer> experience;

    public String getIgn() {
        return ign;
    }

    public CachedPlayer setIgn(String ign) {
        this.ign = ign;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CachedPlayer setEmail(String email) {
        this.email = email;
        return this;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public CachedPlayer setCombatLevel(int combatLevel) {
        this.combatLevel = combatLevel;
        return this;
    }

    public int getSpellBook() {
        return spellBook;
    }

    public CachedPlayer setSpellBook(int spellBook) {
        this.spellBook = spellBook;
        return this;
    }

    public int getWorld() {
        return world;
    }

    public CachedPlayer setWorld(int world) {
        this.world = world;
        return this;
    }

    public HpaLocation getLocation() {
        return location;
    }

    public CachedPlayer setLocation(HpaLocation location) {
        this.location = location;
        return this;
    }

    public Map<String, Integer> getLevels() {
        return levels;
    }

    public CachedPlayer setLevels(Map<String, Integer> levels) {
        this.levels = levels;
        return this;
    }

    public Map<Integer, Integer> getInventory() {
        return inventory;
    }

    public CachedPlayer setInventory(Map<Integer, Integer> inventory) {
        this.inventory = inventory;
        return this;
    }

    public Map<Integer, Integer> getEquipment() {
        return equipment;
    }

    public CachedPlayer setEquipment(Map<Integer, Integer> equipment) {
        this.equipment = equipment;
        return this;
    }

    public Map<Integer, Integer> getBank() {
        return bank;
    }

    public CachedPlayer setBank(Map<Integer, Integer> bank) {
        this.bank = bank;
        return this;
    }

    public Map<Integer, Integer> getQuestProgress() {
        return questProgress;
    }

    public CachedPlayer setQuestProgress(Map<Integer, Integer> questProgress) {
        this.questProgress = questProgress;
        return this;
    }

    public void setExperience(Map<String, Integer> experience) {
        this.experience = experience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CachedPlayer)) {
            return false;
        }

        CachedPlayer player = (CachedPlayer) o;

        if (getCombatLevel() != player.getCombatLevel()) {
            return false;
        }
        if (getSpellBook() != player.getSpellBook()) {
            return false;
        }
        if (getWorld() != player.getWorld()) {
            return false;
        }
        if (getIgn() != null ? !getIgn().equals(player.getIgn()) : player.getIgn() != null) {
            return false;
        }
        if (getEmail() != null ? !getEmail().equals(player.getEmail()) : player.getEmail() != null) {
            return false;
        }
        if (getLocation() != null ? !getLocation().equals(player.getLocation()) : player.getLocation() != null) {
            return false;
        }
        if (getLevels() != null ? !getLevels().equals(player.getLevels()) : player.getLevels() != null) {
            return false;
        }
        if (getInventory() != null ? !getInventory().equals(player.getInventory()) : player.getInventory() != null) {
            return false;
        }
        if (getEquipment() != null ? !getEquipment().equals(player.getEquipment()) : player.getEquipment() != null) {
            return false;
        }
        if (getBank() != null ? !getBank().equals(player.getBank()) : player.getBank() != null) {
            return false;
        }
        return getQuestProgress() != null ? getQuestProgress().equals(player.getQuestProgress()) : player.getQuestProgress() == null;
    }

    @Override
    public int hashCode() {
        int result = getIgn() != null ? getIgn().hashCode() : 0;
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + getCombatLevel();
        result = 31 * result + getSpellBook();
        result = 31 * result + getWorld();
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        result = 31 * result + (getLevels() != null ? getLevels().hashCode() : 0);
        result = 31 * result + (getInventory() != null ? getInventory().hashCode() : 0);
        result = 31 * result + (getEquipment() != null ? getEquipment().hashCode() : 0);
        result = 31 * result + (getBank() != null ? getBank().hashCode() : 0);
        result = 31 * result + (getQuestProgress() != null ? getQuestProgress().hashCode() : 0);
        return result;
    }
}
