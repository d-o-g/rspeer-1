package org.rspeer.injector;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.rspeer.commons.CachedClassLoader;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.adapter.callback.*;
import org.rspeer.injector.adapter.generic.FieldMeta;
import org.rspeer.injector.adapter.generic.InsertFieldAdapter;
import org.rspeer.injector.adapter.generic.InstantiatorAdapter;
import org.rspeer.injector.adapter.generic.SetterAdapter;
import org.rspeer.injector.adapter.hook.*;
import org.rspeer.injector.adapter.wrapper.InjectedWrapper;
import org.rspeer.injector.adapter.wrapper.WrapperAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.network.RSPeerUser;
import org.rspeer.runetek.event.types.SkillEvent;
import org.rspeer.runetek.providers.*;
import org.rspeer.runetek.providers.subclass.SubclassTarget;
import org.rspeer.script.Script;
import org.rspeer.ui.Log;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.objectweb.asm.Opcodes.*;

public final class Injector implements AutoCloseable {

    private final JarInputStream target;
    private final Modscript modscript;
    private final Map<String, ClassNode> library;
    private final Map<String, byte[]> resources;

    public Injector(Modscript modscript, JarInputStream target) {
        this.modscript = modscript;
        this.target = target;
        library = new HashMap<>();
        resources = new HashMap<>();
    }

    public Injector(Modscript modscript, FileInputStream fis) throws IOException {
        this(modscript, new JarInputStream(fis));
    }

    public Injector(Modscript modscript, String file) throws IOException {
        this(modscript, new FileInputStream(file));
    }

    /**
     * Loads the classes from the library
     *
     * @throws IOException
     */
    public void parseLibrary() throws IOException {
        Log.info("Injector", "Parsing library...");
        JarEntry entry;
        while ((entry = target.getNextJarEntry()) != null) {
            if (!entry.getName().endsWith(".class")) {
                //                parseResource(entry);
                continue;
            }
            parseClass();
        }
    }

    /**
     * Processes the classes in the library. This includes any bytecode manipulation
     */
    public void processLibrary() {
        Log.info("Injector", "Injecting library...");

        for (ClassNode cn : library.values()) {
            ClassHook ch = modscript.classes.get(cn.name);
            if (ch != null) {
                processAdapter(cn, new ImplementInterfaceAdapter(modscript, ch, library));

                for (FieldHook fh : ch.getFields()) {
                    processAdapter(cn, new FieldHookGetterAdapter(modscript, fh, library));
                }

                for (MethodHook mh : ch.getMethods()) {
                    String def = mh.getDefinedName();
                    if (def.equals("run") || def.equals("getVarpbit") || mh.getExpectedDesc().equals("omit")) {
                        continue;
                    }
                    processAdapter(cn, new MethodHookInvokerAdapter(modscript, mh, library));
                }
            }
        }

        processInstantiators();
        processModifiers();
        processFieldInsertions();
        processSubclasses();
        processSetters();
        processCallbacks();
        processWrappers();
    }

    private void processInstantiators() {
        ClassNode client = library.get("client");
        CodeAdapter[] adapters = {
                new InstantiatorAdapter(modscript, library, modscript.resolve(RSScriptEvent.class))
        };
        for (CodeAdapter adapter : adapters) {
            processAdapter(client, adapter);
        }
    }

    private void processModifiers() {
        ClassNode client = library.get("client");

        ClassNode boundary = resolve(RSBoundary.class);
        ClassNode boundaryDecor = resolve(RSBoundaryDecor.class);
        ClassNode tileDecor = resolve(RSTileDecor.class);

        ClassHook scene = modscript.resolve(RSSceneGraph.class);

        processAdapter(client, new VarpbitAdapter(modscript, library));

        CodeAdapter[] adapters = {
                new InterfacePositionAdapter(modscript, library),
                new PathingEntityIndexAdapter(modscript, library),
                new GEIndexAdapter(modscript, library),
                // new PickablePositionAdapter(modscript, library),
                new SceneAdapter(modscript, library),
                new ModelAdapter(modscript, library),
                // new CrosshairAdapter(modscript, library),
                new ObjectFloorLevelAdapter(modscript, boundary.name, scene.getMethod("addBoundary"), library),
                new ObjectFloorLevelAdapter(modscript, boundaryDecor.name, scene.getMethod("addBoundaryDecor"), library),
                new ObjectFloorLevelAdapter(modscript, tileDecor.name, scene.getMethod("addTileDecor"), library),
                new BrowserAdapter(modscript, library),
                new SocketAdapter(modscript, library),
                new ChristmasModeAdapter(modscript, library)
                //new ComponentMenuOpcodesAdapter(modscript, library),
                //new PickableWrapperAdapter(modscript, library),
        };

        List<String> cuck = Arrays.asList("eclipseop", "gengsta", "spencer", "buracc", "man16", "tannix", "nigger");
        for (ClassNode cn : library.values()) {
            for (CodeAdapter adapter : adapters) {
                processAdapter(cn, adapter);
            }

            RSPeerUser user = Script.getRSPeerUser();
            if (cuck.contains(user.username.toLowerCase())) {
                //processAdapter(cn, new AntibanAdapter(modscript, library));
            }
        }
    }

    private ClassNode resolve(Class<? extends RSProvider> clazz) {
        return library.get(modscript.resolve(clazz).getInternalName());
    }

    private void processFieldInsertions() {
        ClassNode client = resolve(RSClient.class);
        ClassNode comp = resolve(RSInterfaceComponent.class);
        ClassNode mobile = resolve(RSPathingEntity.class);
        ClassNode pickable = resolve(RSPickable.class);
        ClassNode boundary = resolve(RSBoundary.class);
        ClassNode boundaryDecor = resolve(RSBoundaryDecor.class);
        ClassNode tileDecor = resolve(RSTileDecor.class);
        ClassNode ge = resolve(RSGrandExchangeOffer.class);

        processAdapter(ge, new InsertFieldAdapter(modscript, library, new FieldMeta(false, ge.name, "index", "I"), true));

        processAdapter(client, new InsertFieldAdapter(modscript, library, new FieldMeta(false, client.name, "forcingInteraction", "Z"), true));
        processAdapter(client, new InsertFieldAdapter(modscript, library, new FieldMeta(false, client.name, "forcedAction", "Lorg/rspeer/runetek/api/input/menu/interaction/MenuAction;"), true));

        processAdapter(client, new InsertFieldAdapter(modscript, library, new FieldMeta(false, client.name, "eventMediator", "L" + CodeAdapter.EVENT_PACKAGE + "EventMediator;"), true));
        processAdapter(client, new InsertFieldAdapter(modscript, library, new FieldMeta(false, client.name, "eventDispatcher", "L" + CodeAdapter.EVENT_PACKAGE + "EventDispatcher;"), true));

        processAdapter(comp, new InsertFieldAdapter(modscript, library, new FieldMeta(false, comp.name, "rootX", "I"), true));
        processAdapter(comp, new InsertFieldAdapter(modscript, library, new FieldMeta(false, comp.name, "rootY", "I"), true));

        processAdapter(mobile, new InsertFieldAdapter(modscript, library, new FieldMeta(false, mobile.name, "index", "I"), true));

        processAdapter(boundary, new InsertFieldAdapter(modscript, library, new FieldMeta(false, boundary.name, "floorLevel", "I"), true));
        processAdapter(boundaryDecor, new InsertFieldAdapter(modscript, library, new FieldMeta(false, boundaryDecor.name, "floorLevel", "I"), true));
        processAdapter(tileDecor, new InsertFieldAdapter(modscript, library, new FieldMeta(false, tileDecor.name, "floorLevel", "I"), true));

        //processAdapter(pickable, new InsertFieldAdapter(modscript, library, new FieldMeta(false, pickable.name, "sceneX", "I"), true));
        //processAdapter(pickable, new InsertFieldAdapter(modscript, library, new FieldMeta(false, pickable.name, "sceneY", "I"), true));
        // processAdapter(pickable, new InsertFieldAdapter(modscript, library, new FieldMeta(false, pickable.name, "floorLevel", "I"), true));
    }

    private void processWrappers() {
        for (InjectedWrapper wrapper : InjectedWrapper.values()) {
            ClassHook hook = modscript.resolve(wrapper.getPeerClass());
            if (hook != null) {
                ClassNode node = library.get(hook.getInternalName());
                if (node != null) {
                    String obj = CodeAdapter.PROVIDER_PACKAGE + "RSSceneObject";
                    processAdapter(node, new WrapperAdapter(modscript, library, wrapper,
                            wrapper.getAdapterClass() == SceneObject.class ? obj : null));
                }
            }
        }
    }

    private void processSubclasses() {
        for (SubclassTarget subclass : SubclassTarget.values()) {
            ClassHook ch = modscript.classes.get(subclass.getDefinedName());
            if (ch != null) {
                processAdapter(library.get(ch.getInternalName()), new SuperAdapter(modscript, ch, library, subclass));
            } else {
                Log.severe("Injector", "Subclass error: Unable to find ClassHook for provider " + subclass.getDefinedName());
            }
        }
    }

    private void processSetters() {
        ClassHook client = modscript.resolve(RSClient.class);
        ClassNode clientNode = library.get("client");

        FieldHook redraw = client.getField("redrawMode");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(redraw), (int) redraw.getMultiplier()));

        FieldHook worldSelectorOpen = client.getField("loginWorldSelectorOpen");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(worldSelectorOpen)));

        FieldHook selectedTile = client.getField("selectedRegionTileX");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(selectedTile)));

        selectedTile = client.getField("selectedRegionTileY");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(selectedTile)));

        selectedTile = client.getField("viewportWalking");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(selectedTile)));

        FieldHook loadMembersItemDefinitions = client.getField("loadMembersItemDefinitions");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(loadMembersItemDefinitions)));

        FieldHook onCursorCount = client.getField("onCursorCount");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(onCursorCount), (int) onCursorCount.getMultiplier()));

        FieldHook username = client.getField("username");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(username)));

        FieldHook password = client.getField("password");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(password)));

        FieldHook loginState = client.getField("loginState");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(loginState), (int) loginState.getMultiplier()));

        FieldHook memory = client.getField("lowMemory");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(memory)));

        FieldHook mouse = client.getField("mouseX");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(mouse), (int) mouse.getMultiplier()));

        mouse = client.getField("mouseY");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(mouse), (int) mouse.getMultiplier()));

        mouse = client.getField("clickX");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(mouse), (int) mouse.getMultiplier()));

        mouse = client.getField("clickY");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(mouse), (int) mouse.getMultiplier()));

        mouse = client.getField("timeOfClick");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(mouse), mouse.getMultiplier()));

        mouse = client.getField("previousTimeOfClick");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(mouse), mouse.getMultiplier()));

        mouse = client.getField("mouseMoveTime");
        processAdapter(clientNode, new SetterAdapter(modscript, library, new FieldMeta(mouse), mouse.getMultiplier()));

        ClassHook scriptEvent = modscript.resolve(RSScriptEvent.class);
        ClassNode scriptEventNode = library.get(scriptEvent.getInternalName());
        FieldHook scriptEventArgs = scriptEvent.getField("args");
        processAdapter(scriptEventNode, new SetterAdapter(modscript, library, new FieldMeta(scriptEventArgs)));

        ClassHook playerAppearance = modscript.resolve(RSPlayerAppearance.class);
        ClassNode playerAppearanceNode = library.get(playerAppearance.getInternalName());
        FieldHook playerAppearanceNpc = playerAppearance.getField("transformedNpcId");
        processAdapter(playerAppearanceNode, new SetterAdapter(modscript, library, new FieldMeta(playerAppearanceNpc), (int) playerAppearanceNpc.getMultiplier()));

        ClassHook dyn = modscript.resolve(RSDynamicObject.class);
        ClassNode dynNode = library.get(dyn.getInternalName());
        FieldHook dynId = dyn.getField("id");
        processAdapter(dynNode, new SetterAdapter(modscript, library, new FieldMeta(dynId), (int) dynId.getMultiplier()));

        ClassHook paint = modscript.resolve(RSTilePaint.class);
        ClassNode paintNode = library.get(paint.getInternalName());
        FieldHook paintColor = paint.getField("rgb");
        processAdapter(paintNode, new SetterAdapter(modscript, library, new FieldMeta(paintColor), (int) paintColor.getMultiplier()));

        processPickableSetters(library.get(modscript.resolve(RSPickable.class).getInternalName()));
    }

    private void processPickableSetters(ClassNode cn) {
        String adapter = Pickable.class.getName().replace('.', '/');
        cn.fields.add(new FieldNode(ACC_PUBLIC, "wrapper", "L" + adapter + ";", null, null));
        MethodNode getter = new MethodNode(ACC_PUBLIC, "getWrapper", "()" + "L" + adapter + ";", null, null);
        getter.instructions.add(new VarInsnNode(ALOAD, 0));
        getter.instructions.add(new FieldInsnNode(GETFIELD, cn.name, "wrapper", "L" + adapter + ";"));
        getter.instructions.add(new InsnNode(ARETURN));
        cn.methods.add(getter);
    }

    private void processCallbacks() {
        ClassHook mobile = modscript.resolve(RSPathingEntity.class);

        MethodHook onHitUpdate = mobile.getMethod("addHitUpdate");
        MethodHook onHitsplat = mobile.getMethod("addHitSplat");

        MethodHook updateNpcs = modscript.resolve(RSClient.class).getMethod("updateNpcs");

        FieldHook target = mobile.getField("targetIndex");

        CodeAdapter[] callbacks = {
                new MessageCallback(modscript, library),
                new RenderCallback(modscript, library),
                // new LoginMessageCallback(modscript, library),
                new ProcessActionCallback(modscript, library),
                new RuneScriptCallback(modscript, library),
                new GameStateCallback(modscript, library),
                new SetWorldCallback(modscript, library),
                //new EntityHoverCallback(modscript, library),
                new BuildMenuCallback2(modscript, library),
                new BuildComponentMenuCallback(modscript, library),
                new EngineTickCallback(modscript, library),
                new SpawnObjectCallback(modscript, library),
                new VarpCallback(modscript, library),
                new ItemTableCallback(modscript, library),
                new MapRegionCallback(modscript, library),
                new CallbackAtStartOfMethod(modscript, library, onHitUpdate, "onHitUpdate"),
                new CallbackAtStartOfMethod(modscript, library, updateNpcs, "updateNpcs"),
                new CallbackAtStartOfMethod(modscript, library, onHitsplat, "onHitsplat"),
                new MouseMotionCallback(modscript, library),
                new KeyInputCallback(modscript, library),
                new OutgoingPacketCallback(modscript, library),
                new SkillCallback(modscript, library, SkillEvent.TYPE_TEMPORARY_LEVEL),
                new SkillCallback(modscript, library, SkillEvent.TYPE_LEVEL),
                new SkillCallback(modscript, library, SkillEvent.TYPE_EXPERIENCE),
                new CollisionCallback(modscript, library),
                new ProcessActionSetter(modscript, library),
                new ReflectiveInvocationCallback(modscript, library),
                new NpcSpawnCallback(modscript, library),
                new ProjectileMovedCallback(modscript, library),
                new LoginResponseCallback(modscript, library),
                new RandomInterceptorAdapter(modscript, library),
                new MouseActionSnapshotAdapter(modscript, library),
                new MouseRecordSnapshotAdapter(modscript, library),
                new PutInsnCallback(modscript, library, mobile.getField("targetIndex"))
                        .setOwnerPredicate(x -> x.equals(modscript.resolve(RSNpc.class).getInternalName()) || x.equals(modscript.resolve(RSPlayer.class).getInternalName())
                ),
                new PutInsnCallback(modscript, library, mobile.getField("animation"))
                        .setOwnerPredicate(x -> x.equals(modscript.resolve(RSNpc.class).getInternalName()) || x.equals(modscript.resolve(RSPlayer.class).getInternalName())
                ),
                new PutInsnCallback(modscript, library, modscript.resolve(RSClient.class).getField("loginResponse3")),

                new TypedDequeInsertionCallback(modscript,
                        x -> x.classes.get("Client").getField("graphicsObjectDeque"),
                        modscript.classes.get("GraphicsObject"),
                        "notifyGraphicSpawn",
                        library),

                new TypedDequeInsertionCallback(modscript,
                        x -> x.classes.get("Client").getField("projectileDeque"),
                        modscript.classes.get("Projectile"),
                        "notifyProjectileSpawn",
                        library)
        };

        for (ClassNode cn : library.values()) {
            for (CodeAdapter adapter : callbacks) {
                processAdapter(cn, adapter);
            }
        }
    }

    /**
     * Invokes an adapter on the given ClassNode and it's methods
     *
     * @param target  The ClassNode to adapt
     * @param adapter The adapter
     */
    private void processAdapter(ClassNode target, CodeAdapter adapter) {
        adapter.visit(target);
    }

    /**
     * Writes the classes to the injected gamepack and loads them
     *
     * @return A ClassLoader
     * @throws Exception
     */
    public ClassLoader writeAndLoad() throws Exception {
        Map<String, byte[]> injected = new HashMap<>();
        for (ClassNode cn : library.values()) {
            ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(w);
            injected.put(cn.name, w.toByteArray());
        }
        return new CachedClassLoader(injected);
    }

    /**
     * Reads a single ClassNode
     *
     * @throws IOException
     */
    private void parseClass() throws IOException {
        ClassNode cn = new ClassNode();
        ClassReader reader = new ClassReader(target);
        reader.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        library.put(cn.name, cn);
    }

    /**
     * Parses a resource entry in the jar file
     *
     * @param entry
     * @throws IOException
     */
    private void parseResource(JarEntry entry) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        byte[] buffer = new byte[128];
        while ((read = target.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        resources.put(entry.getName(), out.toByteArray());
        out.close();
    }

    @Override
    public void close() throws Exception {
        target.close();
        library.clear();
        resources.clear();
    }
}
