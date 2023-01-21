package org.rspeer.runetek.providers;

import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.input.menu.interaction.MenuAction;
import org.rspeer.runetek.event.EventDispatcher;
import org.rspeer.runetek.event.EventMediator;
import org.rspeer.runetek.providers.annotations.Synthetic;

import java.applet.Applet;
import java.util.Map;

public interface RSClient extends RSGameEngine {

    default Applet asApplet() {
        return (Applet) this;
    }

    void messageReceived(int type, String source, String message, String channel);

    void absoluteToViewport(int x, int y, int z);

    void addAxisAlignedBoundingBox(RSModel m, int x, int z, int y, int color);

    void addLegacy2DBoundingBox(int minX, int minY, int maxX, int maxY, int color);

    int[] boundingBoxToViewport(int i, int i2, int i3);

    int[][][] getDynamicSceneData();

    RSConnectionContext getConnectionContext();

    RSAnimationSequence getAnimationSequence(int id);

    RSDefinitionProperty getDefinitionProperty(int id);

    RSGraphicDefinition getGraphicDefinition(int id);

    RSHitsplatDefinition getHitsplatDefinition(int id);

    RSItemDefinition getItemDefinition(int id);

    void setGameState(int state);

    RSSprite getItemSprite(int itemId, int itemAmount, int borderThickness, int shadowColor, int stackable, boolean noted);

    RSNpcDefinition getNpcDefinition(int id);

    RSObjectDefinition getObjectDefinition(int id);

    RSCacheReferenceTable getReferenceTable(int i, boolean z, boolean z2, boolean z3);

    RSVarpbit getVarpbit(int id);

    void insertMenuItem(String action, String target, int opcode, int primary, int secondary, int tertiary);

    RSAudioTrack loadAudioTrack(RSReferenceTable rt, int i, int i2);

    boolean loadWorlds();

    void onSceneXTEAKeyChange(boolean instanced);

    void processAction(int secondary, int tertiary, int opcode, int primary, String action, String target, int crosshairDrawX, int crosshairDrawY);

    void resetDrawingArea();

    void setWorld(RSWorld w);

    byte[] getRandom();

    int getAudioEffectCount();

    int getBaseX();

    int getBaseY();

    int getBootState();

    int getCameraPitch();

    int getCameraX();

    int getCameraY();

    int getCameraYaw();

    int getCameraZ();

    int getCanvasHeight();

    int getCanvasWidth();

    int getClickX();

    int getClickY();

    int getGameState();

    int getCurrentWorld();

    int getCurrentWorldMask();

    int getCursorState();

    int getDestinationX();

    int getDestinationY();

    int getDrawingAreaBottom();

    int getDrawingAreaHeight();

    int getDrawingAreaLeft();

    int getDrawingAreaRight();

    int getDrawingAreaTop();

    int getDrawingAreaWidth();

    int getEnergy();

    int getEngineCycle();

    int getBuild();

    int getHintArrowNpcIndex();

    int getHintArrowPlayerIndex();

    int getHintArrowZ(); //like the height/size

    int getHintArrowType();

    int getHintArrowX();

    int getHintArrowY();

    int getItemSelectionState();

    int getLatestSelectedItemIndex();

    int getFloorLevel();

    int getLoginState();

    int getMapRotation();

    int getMenuHeight();

    int getMenuRowCount();

    int getMenuWidth();

    int getMenuX();

    int getMenuY();

    int getMouseIdleTime();

    int getMouseX();

    int getMouseY();

    long getMouseMoveTime();

    long getTimeOfClick();

    long getPreviousTimeOfClick();

    @Synthetic
    void setMouseMoveTime(long mouseMoveTime);

    @Synthetic
    void setTimeOfClick(long timeOfClick);

    @Synthetic
    void setPreviousTimeOfClick(long previousTimeOfClick);

    @Synthetic
    void setMouseX(int x);

    @Synthetic
    void setMouseY(int y);

    @Synthetic
    void setClickX(int x);

    @Synthetic
    void setClickY(int y);

    int getOnCursorCount();

    @Synthetic
    void setOnCursorCount(int onCursorCount);

    int getPacketId();

    int getPendingClickX();

    int getPendingClickY();

    int getPendingMouseX();

    int getPendingMouseY();

    int getPlayerIndex();

    int getPublicChatMode();

    int getTradeChatMode();

    int getRedrawMode();

    @Synthetic
    void spawnObjectLater(int floorLevel, int sceneX, int sceneY, int stubType, int id, int type, int rotation, int delay, int hitpoints);

    @Synthetic
    void setRedrawMode(int redrawMode);

    int getRights();

    int getSelectedRegionTileX();

    @Synthetic
    void setSelectedRegionTileX(int newX);

    int getSelectedRegionTileY();

    @Synthetic
    void setSelectedRegionTileY(int newY);

    @Synthetic
    void setViewportWalking(boolean viewportWalking);

    int getSpellTargetFlags();

    int getViewportHeight();

    int getViewportScale();

    int getViewportWidth();

    int getWeight();

    RSMouseRecorder getMouseRecorder();

    RSPlayer getPlayer();

    RSActionPrioritySetting getNpcActionPriority();

    RSActionPrioritySetting getPlayerActionPriority();

    RSClientPreferences getPreferences();

    RSSceneGraph getSceneGraph();

    RSPacketBuffer getPacketBuffer();

    RSConnection getConnection();

    RSNodeDeque<RSGraphicsObject> getGraphicsObjectDeque();

    RSNodeDeque<RSPendingSpawn> getPendingSpawns();

    RSNodeDeque<RSProjectile> getProjectileDeque();

    RSNodeTable<RSIntegerNode> getInterfaceConfigs();

    RSNodeTable<RSInterfaceNode> getInterfaceNodes();

    RSNodeTable<RSItemTable> getItemTables();

    RSLinkedList getBoundingBoxes();

    RSLinkedList getClassStructures();

    String getCurrentDomain();

    String getLatestSelectedItemName();

    String getLoginResponse1();

    String getLoginResponse2();

    String getLoginResponse3();

    String getPassword();

    String getUsername();

    RSFont getFont_p12full();

    RSBoundingBoxDrawType getBoundingBoxDrawType();

    boolean isCameraLocked();

    boolean isDrawingAABB();

    boolean isInInstancedScene();

    boolean isLoginWorldSelectorOpen();

    void setLoginWorldSelectorOpen(boolean open);

    boolean isLowMemory();

    boolean isMembersWorld();

    @Synthetic
    void setLoadMembersItemDefinitions(boolean loadMembersItemDefinitions);

    boolean isMenuOpen();

    default boolean isResizableMode() {
        return Functions.mapOrElse(this::getPreferences, RSClientPreferences::isResizable);
    }

    boolean isSpellSelected();

    boolean isViewportWalking();

    int[] getCurrentLevels();

    int[] getDrawingAreaPixels();

    int[] getExperiences();

    int[] getInterfaceHeights();

    int[] getInterfacePositionsX();

    int[] getInterfacePositionsY();

    int[] getInterfaceWidths();

    int[] getLevels();

    int[] getMapRegions();

    int[] getMenuOpcodes();

    int[] getMenuPrimaryArgs();

    int[] getMenuSecondaryArgs();

    int[] getMenuTertiaryArgs();

    int[] getNpcIndices();

    int getNpcCount();

    int[] getPlayerIndices();

    int getPlayerCount();

    long[] getOnCursorUids();

    int[] getTempVarps();

    int[] getVarps();

    short[] getGrandExchangeSearchResults();

    Map<Integer, RSChatstream> getChatstreams();

    default RSChatstream getChatstream(int type) {
        return getChatstreams().get(type);
    }

    default RSChatter[] getClanMembers() {
        if (getClanSystem() == null) {
            return new RSChatter[0];
        }
        return getClanSystem().getChatters();
    }

    default RSChatter[] getBefriendedPlayers() {
        if (getSocialSystem() == null || getSocialSystem().getFriendListContext() == null) {
            return new RSChatter[0];
        }
        return getSocialSystem().getFriendListContext().getChatters();
    }

    default RSChatter[] getIgnoredPlayers() {
        if (getSocialSystem() == null || getSocialSystem().getIgnoreListContext() == null) {
            return new RSChatter[0];
        }
        return getSocialSystem().getIgnoreListContext().getChatters();
    }

    RSClanSystem getClanSystem();

    RSSocialSystem getSocialSystem();

    RSPlayer[] getPlayers();

    RSAudioEffect[] getAudioEffects();

    RSWorld[] getWorlds();

    RSNpc[] getNpcs();

    RSCameraCapture[] getCameraCaptures();

    RSCollisionMap[] getCollisionMaps();

    RSGrandExchangeOffer[] getGrandExchangeOffers();

    RSMenuItem getEventMenuItem();

    String[] getMenuActions();

    String[] getMenuTargets();

    boolean[] getMenuShiftClickActions();

    String[] getPlayerActions();

    boolean[] getPlayerActionPriorities();

    int[][] getXteaKeys();

    RSInterfaceComponent[][] getInterfaces();

    boolean[] getValidInterfaces();

    int getRootInterfaceIndex();

    byte[][][] getSceneRenderRules();

    int[][][] getTileHeights();

    RSNodeDeque<RSPickable>[][][] getPickableNodeDeques();

    @Synthetic
    EventMediator getEventMediator();

    @Synthetic
    void setEventMediator(EventMediator mediator);

    @Synthetic
    EventDispatcher getEventDispatcher();

    @Synthetic
    void setEventDispatcher(EventDispatcher dispatcher);

    @Synthetic
    boolean isForcingInteraction();

    @Synthetic
    void setForcingInteraction(boolean forcingInteraction);

    @Synthetic
    MenuAction getForcedAction();

    @Synthetic
    void setForcedAction(MenuAction action);

    default RSNpc getNpc(int index) {
        return index < 0 || index > 32767 ? null : getNpcs()[index];
    }

    default RSPlayer getPlayer(int index) {
        return index < 0 || index > 2047 ? null : getPlayers()[index];
    }

    @Synthetic
    void setUsername(String username);

    @Synthetic
    void setPassword(String password);

    @Synthetic
    void setLoginState(int state);

    RSInterfaceComponent getPleaseWaitComponent();

    @Synthetic
    void setLowMemory(boolean lowMemory);

    @Synthetic
    RSScriptEvent newScriptEvent();

    @Synthetic
    void fireScriptEvent(RSScriptEvent event);

    @Synthetic
    default void fireScriptEvent(int id, Object... args) {
        RSScriptEvent event = newScriptEvent();
        Object[] rawArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, rawArgs, 1, args.length);
        rawArgs[0] = id;
        event.setArgs(rawArgs);
        try {
            fireScriptEvent(event);
        } catch (Exception ignored) {

        }
    }
}