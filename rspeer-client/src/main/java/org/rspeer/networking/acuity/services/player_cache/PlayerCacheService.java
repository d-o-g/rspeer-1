package org.rspeer.networking.acuity.services.player_cache;

import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.networking.acuity.AcuityServices;
import org.rspeer.networking.acuity.services.player_cache.domain.CachedPlayer;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaLocation;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.*;
import org.rspeer.runetek.event.types.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 7/30/2018.
 */
public class PlayerCacheService implements SkillListener, WorldChangeListener, VarpListener, ItemTableListener, LoginResponseListener, BankLoadListener {

    public static PlayerCacheService INSTANCE;

    private CachedPlayer cachedPlayer = null;

    private ScheduledFuture<?> scheduledUpdate;

    private PlayerCacheService() {
        RsPeerExecutor.scheduleAtFixedRate(this::update, 2, 20, TimeUnit.SECONDS);
        Game.getEventDispatcher().register(this);
    }

    public static void start() {
        if (INSTANCE != null) {
            return;
        }
        INSTANCE = new PlayerCacheService();
    }

    private void update() {
        if (Game.getClient() == null || !Game.isLoggedIn()) {
            return;
        }

        try {
            this.cachedPlayer = build();
        } catch (Throwable e) {
            AcuityServices.onException(e);
        }
    }

    public CachedPlayer build() {
        Player local = Players.getLocal();
        if (local == null) {
            return null;
        }

        CachedPlayer cachedPlayer = new CachedPlayer();
        cachedPlayer.setLocation(new HpaLocation(local.getX(), local.getY(), local.getFloorLevel()));
        cachedPlayer.setWorld(Worlds.getCurrent());
        cachedPlayer.setIgn(local.getName());
        cachedPlayer.setEmail(Game.getClient().getUsername());
        cachedPlayer.setSpellBook(Magic.getSpellBook());
        cachedPlayer.setCombatLevel(local.getCombatLevel());

        Map<String, Integer> levels = new HashMap<>();
        for (Skill skill : Skill.values()) {
            levels.put(skill.name(), Skills.getCurrentLevel(skill));
        }
        cachedPlayer.setLevels(levels);


        Map<String, Integer> experience = new HashMap<>();
        for (Skill skill : Skill.values()) {
            experience.put(skill.name(), Skills.getExperience(skill));
        }
        cachedPlayer.setExperience(experience);

        Map<Integer, Integer> inventory = new HashMap<>();
        for (Item item : Inventory.getItems()) {
            inventory.put(item.getId(), item.getStackSize() + inventory.getOrDefault(item.getId(), 0));
        }
        cachedPlayer.setInventory(inventory);

        Map<Integer, Integer> equipment = new HashMap<>();
        for (EquipmentSlot slot : Equipment.getOccupiedSlots()) {
            equipment.put(slot.getItemId(), slot.getItemStackSize() + equipment.getOrDefault(slot.getItemId(), 0));
        }
        cachedPlayer.setEquipment(equipment);

        if (Bank.isOpen()) {
            Map<Integer, Integer> bank = new HashMap<>();
            for (Item item : Bank.getItems()) {
                bank.put(item.getId(), item.getStackSize() + bank.getOrDefault(item.getId(), 0));
            }
            cachedPlayer.setBank(bank);
        }

        //Game.getClient().fireScriptEvent(1353, 26148871, 26148872, 26148873);
        Map<Integer, Integer> questProgress = new HashMap<>();
        cachedPlayer.setQuestProgress(questProgress);

        return cachedPlayer;
    }

    public CachedPlayer getCachedPlayer() {
        if (cachedPlayer == null) {
            update();
        }
        return cachedPlayer;
    }

    @Override
    public void notify(SkillEvent e) {
        scheduleUpdate();
    }

    @Override
    public void notify(WorldChangeEvent event) {
        scheduleUpdate();
    }

    @Override
    public void notify(VarpEvent e) {
        scheduleUpdate();
    }

    @Override
    public void notify(ItemTableEvent e) {
        if (e.getTableKey() != ItemTables.BANK) {
            scheduleUpdate();
        }
    }

    private void scheduleUpdate() {
        if (scheduledUpdate != null) {
            scheduledUpdate.cancel(false);
        }
        scheduledUpdate = RsPeerExecutor.schedule(this::update, 2, TimeUnit.SECONDS);
    }

    @Override
    public void notify(BankLoadEvent event) {
        scheduleUpdate();
    }

    @Override
    public void notify(LoginResponseEvent e) {
        scheduleUpdate();
    }
}
