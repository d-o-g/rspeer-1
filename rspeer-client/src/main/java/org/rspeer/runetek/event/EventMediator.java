package org.rspeer.runetek.event;

import org.rspeer.commons.ProxySocketProvider;
import org.rspeer.commons.SocketProvider;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Projectile;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.StringCommons;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.InterfaceComposite;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.input.Mouse;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.input.menu.ContextMenu;
import org.rspeer.runetek.api.movement.pathfinding.hpa.HpaGenerationData;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.Projection;
import org.rspeer.runetek.api.scene.Scene;
import org.rspeer.runetek.event.snapshot.DefaultMouseSnapshotProvider;
import org.rspeer.runetek.event.snapshot.MouseActionRecord;
import org.rspeer.runetek.event.snapshot.MouseMotionRecord;
import org.rspeer.runetek.event.snapshot.MouseSnapshotProvider;
import org.rspeer.runetek.event.types.*;
import org.rspeer.runetek.providers.*;
import org.rspeer.runetek.providers.annotations.ClientInvoked;
import org.rspeer.ui.BotAd;
import org.rspeer.ui.Log;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;

/**
 * Methods in this class are invoked by the game client
 */
public final class EventMediator {

    private static final InterfaceAddress WORLD_MAP_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(InterfaceComposite.MINIMAP.getGroup(), x -> x.containsAction("Fullscreen"))
    );

    /*private static final InterfaceAddress RESIZABLE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(InterfaceComposite.OPTIONS_TAB.getGroup(), x -> x.containsAction("Resizable mode"))
    );*/

    private static final InterfaceAddress[] BLOCKED_ADDRESSES = {
            WORLD_MAP_ADDRESS, //RESIZABLE_ADDRESS
    };

    private static final String[] BLOCKED_URL_INFIX = {
            "welcome-back",
            "m=news",
    };

    private SocketProvider socketProvider = new ProxySocketProvider();
    private MouseSnapshotProvider mouseSnapshotProvider = new DefaultMouseSnapshotProvider();

    private BotAd advertisement = new BotAd();
    private boolean debugMenuActions = false;
    private EventMediatorExceptionCallback exceptionCallback;
    private Object[] lastScript = new Object[0];

    public EventMediator(EventMediatorExceptionCallback exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
    }

    public void setMouseSnapshotProvider(MouseSnapshotProvider mouseSnapshotProvider) {
        this.mouseSnapshotProvider = mouseSnapshotProvider;
    }

    @ClientInvoked
    public void notifyProjectileSpawn(RSProjectile projectile) {
        try {
            Game.getEventDispatcher().immediate(new ProjectileSpawnEvent(projectile.getWrapper()));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void notifyGraphicSpawn(RSGraphicsObject object) {
        try {
            Game.getEventDispatcher().immediate(new GraphicSpawnEvent(object));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void notifyMouseMotionSnapshot(RSMouseRecorder recorder) {
        int index = recorder.getIndex();
        if (index < 0 || index >= 500) {
            return;
        }

        long[] time = recorder.getTimeHistory();
        int[] x = recorder.getXHistory();
        int[] y = recorder.getYHistory();

        MouseMotionRecord original = new MouseMotionRecord(index, time[index], x[index], y[index]);
        MouseMotionRecord transform = mouseSnapshotProvider.interceptMotionRecord(original);
        if (original == transform) {
            return;
        }

        time[index] = transform.getTime();
        x[index] = transform.getX();
        y[index] = transform.getY();

        //update the rest just in case...
        RSClient client = Game.getClient();
        client.setMouseMoveTime(transform.getTime());
        client.setMouseX(transform.getX());
        client.setMouseY(transform.getY());
    }

    @ClientInvoked
    public void notifyMouseActionSnapshot() {
        RSClient client = Game.getClient();
        MouseActionRecord original = new MouseActionRecord(
                client.getTimeOfClick(),
                client.getPreviousTimeOfClick(),
                client.getClickX(),
                client.getClickY()
        );

        //TODO maybe set click meta too?
        MouseActionRecord transform = mouseSnapshotProvider.interceptActionRecord(original);
        if (original == transform) {
            return;
        }

        client.setTimeOfClick(transform.getTime());
        client.setPreviousTimeOfClick(transform.getPreviousActionTime());
        client.setClickX(transform.getX());
        client.setClickY(transform.getY());
    }

    @ClientInvoked
    public void notifyMouseActionPacketSent() {
        RSClient client = Game.getClient();
        client.setClickX(Mouse.getX());
        client.setClickY(Mouse.getY());
    }

    private void onException(Exception e) {
        if (exceptionCallback != null) {
            exceptionCallback.execute(e);
        }
    }

    @ClientInvoked
    public byte[] getRandom(byte[] original) {
        return new byte[24];
    }

    @ClientInvoked
    public Socket getSocket(Socket base) {
        try {
            socketProvider.init(base.getInetAddress().getHostAddress(), base.getPort());
            return socketProvider.provide();
        } catch (IOException e) {
            onException(e);
            return null;
        }
    }

    @ClientInvoked
    public void npcSpawned(RSNpc spawn) {
        try {
            Npc npc = spawn.getWrapper();
            if (!npc.getName().equals("null")) {
                Game.getEventDispatcher().immediate(new NpcSpawnEvent(npc));
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void projectileMoved(RSProjectile projectile, int targetX, int targetY) {
        Projectile project = projectile.getWrapper();
        ProjectileMoveEvent event = new ProjectileMoveEvent(project, targetX, targetY);

        try {
            Game.getEventDispatcher().immediate(event);
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public boolean canBrowse(String url) {
        try {
            for (String block : BLOCKED_URL_INFIX) {
                if (url.contains(block)) {
                    return false;
                }
            }
        } catch (Exception e) {
            onException(e);
        }
        return true;
    }

    @ClientInvoked
    public void mapRegionChanged(int index) {
        try {
            Game.getEventDispatcher().immediate(new MapRegionChangedEvent(index));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void messageReceived(int type, String source, String message, String channel) {
        try {
            Game.getEventDispatcher().immediate(new ChatMessageEvent(StringCommons.replaceJagspace(source), message, channel, type));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void render(RSGraphicsProvider provider) {
        try {
            Image image = provider.getImage();
            if (image != null) {
                Graphics g = image.getGraphics().create();

                RenderEvent e = new RenderEvent(g, provider);
                Game.getEventDispatcher().immediate(e);
                advertisement.notify(e);
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void processLoginResponse(int code) {
        try {
            LoginResponseEvent event = new LoginResponseEvent(code);
            Game.getEventDispatcher().immediate(event);
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void onLoginError(String responseLine1, String responseLine2, String responseLine3) {
        try {
            LoginMessageEvent e = new LoginMessageEvent(responseLine1, responseLine2, responseLine3);
            Game.getEventDispatcher().immediate(e);
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void actionProcessed(int secondaryArg, int tertiaryArg, int opcode, int primaryArg,
            String action, String target, int clickX, int clickY) {
        try {
            Game.getEventDispatcher().immediate(new MenuActionEvent(secondaryArg, tertiaryArg, opcode, primaryArg, action, target));
            if (debugMenuActions) {
                String verbose = ActionOpcodes.verbose(opcode, primaryArg, secondaryArg, tertiaryArg);
                if (!verbose.isEmpty()) {
                    Log.info(verbose);
                }
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void postActionProcessed(int secondaryArg, int tertiaryArg, int opcode, int primaryArg,
            String action, String target,
            int clickX, int clickY) {
        //this is necessary, it clears the first action and hovered entity that we've set.
        Game.getClient().setForcingInteraction(false);
    }

    @ClientInvoked
    public boolean filterAction(int secondaryArg, int tertiaryArg, int opcode, int primaryArg,
            String action, String target, int clickX, int clickY) {
        if (opcode == ActionOpcodes.INTERFACE_ACTION) {
            for (InterfaceAddress addr : BLOCKED_ADDRESSES) {
                if (addr.isMapped() || addr.resolve() != null) {
                    int group = tertiaryArg >> 16;
                    int comp = tertiaryArg & 0xffff;
                    if (group == addr.getRoot() && comp == addr.getComponent() && secondaryArg == addr.getSubComponent()) {
                        Log.info("Usage of this game component is disabled by RSPeer");
                        return true;
                    }
                }
            }
        }
        //make it return true based on params to filter out actions
        return false;
    }

    @ClientInvoked
    public void gameStateChanged(int newState) {
        try {
            int oldState = Game.getClient().getGameState();
            if (oldState == 5 && newState == 10) {
                //login screen just loaded
                Time.sleep(100);
                Definitions.populate();
                Game.getClient().setRedrawMode(420);
            }
            if (oldState != newState) {
                Game.getEventDispatcher().immediate(new GameStateEvent(oldState, newState));
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void worldChanged(RSWorld newWorld) {
        try {
            RSWorld oldWorld = Worlds.get(Game.getClient().getCurrentWorld());
            if (oldWorld != newWorld) {
                Game.getEventDispatcher().immediate(new WorldChangeEvent(oldWorld, newWorld));
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void onHitUpdate(RSPathingEntity entity, int id, int startCycle,
            int currentCycle, int duration, int startWidth, int currentWidth) {
        try {
            if (startWidth == 0) {
                Game.getEventDispatcher().immediate(new DeathEvent(entity.getWrapper()));
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void updateNpcs() {
        try {
            Game.getEventDispatcher().immediate(new TickEvent(TickEvent.Type.SERVER));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void onHitsplat(RSPathingEntity entity, int type, int damage, int id, int special, int idk1, int idk2) {
        try {
            //idk1 and idk2 are probably startCycle and duration
            Game.getEventDispatcher().immediate(new HitsplatEvent(entity.getWrapper(), type, damage, id));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void spawnObjectLater(int floorLevel, int sceneX, int sceneY, int stubType, int id, int type, int orientation, int delay, int hitpoints) {
        try {
            Game.getEventDispatcher().immediate(new ObjectSpawnEvent(floorLevel, sceneX, sceneY, stubType, id, type, orientation, delay, hitpoints));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void onEngineTick() {
        try {
            if (Projection.getTickDelay() > 0) {
                Time.sleep(Projection.getTickDelay());
            }
            //kinda useless tbh
            //Game.getEventDispatcher().immediate(new TickEvent(TickEvent.Type.ENGINE));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void varpChanged(int index, int oldValue, int newValue) {
        try {
            if (oldValue != newValue) {
                Game.getEventDispatcher().immediate(new VarpEvent(index, oldValue, newValue));
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void itemTableChange(int tableKey, int index, int id, int amount) {
        try {
            if (amount != -1 && index != -1) {
                RSItemTable table = ItemTables.lookup(tableKey);
                if (table != null) {
                    Game.getEventDispatcher().immediate(new ItemTableEvent(table, tableKey, index, id, amount));
                }
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void mapRegionChanged() {
    }

    @ClientInvoked
    public void onProcessClassStructure(Method target, Object[] args) {
        try {

            Game.getEventDispatcher().immediate(new ReflectionEvent(target, args));

        } catch (Exception ex) {
            onException(ex);
        }
    }

    @ClientInvoked
    public void mouseEvent(MouseEvent e) {
        try {
            Game.getEventDispatcher().immediateInput(e);
            advertisement.notify(e);
        } catch (Exception ex) {
            onException(ex);
        }
    }

    @ClientInvoked
    public void keyEvent(KeyEvent e) {
        try {
            Game.getEventDispatcher().immediateInput(e);
        } catch (Exception ex) {
            onException(ex);
        }
    }

    @ClientInvoked
    public void notifySkillUpdate(int index, int current, int type) {
        try {
            Skill[] skills = Skill.values();
            if (index >= 0 && index < skills.length) {
                int prev = Skills.getExperience(skills[index]);
                if (type == SkillEvent.TYPE_TEMPORARY_LEVEL) {
                    prev = Skills.getCurrentLevel(skills[index]);
                } else if (type == SkillEvent.TYPE_LEVEL) {
                    prev = Skills.getLevel(skills[index]);
                }

                if (prev == current) {
                    return;
                }

                if (type == SkillEvent.TYPE_EXPERIENCE) {
                    int prevLevel = Skills.getLevelAt(prev);
                    int currentLevel = Skills.getLevelAt(current);
                    if (prevLevel != currentLevel) {
                        Game.getEventDispatcher().immediate(new SkillEvent(skills[index], SkillEvent.TYPE_LEVEL, prevLevel, currentLevel));
                    }
                }
                Game.getEventDispatcher().immediate(new SkillEvent(skills[index], type, prev, current));
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void notifyCollisionUpdate() {
        //HpaGenerationData.sendCollisionMap();
        Scene.setLastCollisionUpdate(System.currentTimeMillis());
    }

    @ClientInvoked
    public void setTargetIndex(RSPathingEntity entity, int targetIndex) {
        try {
            int oldTargetIndex = entity.getTargetIndex();
            if (oldTargetIndex == 65535 || targetIndex == 65535 || oldTargetIndex == targetIndex) {
                return;
            }
            Game.getEventDispatcher().immediate(new TargetEvent(entity.getWrapper(), oldTargetIndex, targetIndex));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void setLoginResponse3(String line3) {
        try {
            Game.getEventDispatcher().immediate(new LoginMessageEvent(
                    Game.getClient().getLoginResponse1(), Game.getClient().getLoginResponse2(), line3
            ));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void setAnimation(RSPathingEntity entity, int animation) {
        try {
            if (entity.getAnimation() == -1 && animation == -1) {
                return;
            }
            Game.getEventDispatcher().immediate(new AnimationEvent(entity.getWrapper(), entity.getAnimation(), animation));
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public void writePacket(RSOutgoingPacket packet) {
        try {
            if (packet != null && packet.getMeta() != null) {
                Game.getEventDispatcher().immediate(new OutgoingPacketEvent(packet));
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    @ClientInvoked
    public boolean fireScriptEvent(RSScriptEvent event) {
        try {
            Object[] args = event.getArgs();
            if (Arrays.equals(lastScript, args)) {
                return true;
            }

            //script 489 = world map
            //script 1495 = bank load
            lastScript = args;
            if (args[0] instanceof Integer) {
                int scriptId = (int) args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
                if (scriptId == 1495) {
                    Game.getEventDispatcher().immediate(new BankLoadEvent(event));
                }

               /* if (scriptId != 1004 && scriptId != 839 && scriptId != 283 && scriptId != 846) {
                    System.out.println("Script" + scriptId + Arrays.toString(args));
                }*/
            }
        } catch (Exception e) {
            onException(e);
        }
        return true;
    }

    @ClientInvoked
    public void buildComponentMenu() {
        buildMenu();
    }

    @ClientInvoked
    public void buildMenu() {
        try {
            if (Game.getClient().isForcingInteraction() && Game.getClient().getForcedAction() != null) {
                int index = ContextMenu.getRowCount() - 1;
                if (index < 0 || index > 399) {
                    index = 0;
                }

                if (Game.getClient().getMenuOpcodes()[index] == ActionOpcodes.INTERFACE_ACTION_2) {
                    ContextMenu.getShiftClickActions()[index] = true;
                }
            }
        } catch (Exception e) {
            onException(e);
        }
    }

    public boolean isDebugMenuActions() {
        return debugMenuActions;
    }

    public void setDebugMenuActions(boolean debugMenuActions) {
        this.debugMenuActions = debugMenuActions;
    }
}
