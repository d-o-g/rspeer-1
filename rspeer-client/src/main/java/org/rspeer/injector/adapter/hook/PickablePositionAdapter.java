package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.runetek.providers.RSClient;
import org.rspeer.runetek.providers.RSPickable;

import java.util.Map;

/**
 * TODO will clean up later lol
 */
public final class PickablePositionAdapter extends CodeAdapter {

    private final FieldHook level;
    private final FieldHook pickables;
    private final String item;

    public PickablePositionAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, library);
        ClassHook client = modscript.resolve(RSClient.class);
        level = client.getField("floorLevel");
        pickables = client.getField("pickableNodeDeques");
        item = modscript.resolve(RSPickable.class).getInternalName();
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            //haha imagine using an actual instruction searcher
            if (ain != null && ain.getOpcode() == GETSTATIC
                    && (ain.getNext().getOpcode() == LDC
                    || ain.getNext().getOpcode() == GETSTATIC)
                    && (ain.getNext().getNext().getOpcode() == LDC
                    || ain.getNext().getNext().getOpcode() == GETSTATIC)
                    && ain.getNext().getNext().getNext().getOpcode() == IMUL) {
                FieldInsnNode deque = (FieldInsnNode) ain;
                FieldInsnNode floor = (FieldInsnNode) (ain.getNext().getOpcode() == GETSTATIC ? ain.getNext() : ain.getNext().getNext());
                if (level.match(floor) && pickables.match(deque)) {
                    AbstractInsnNode arr = ain.getNext().getNext().getNext().getNext();
                    //haha sick validation
                    if (arr != null && arr.getOpcode() == AALOAD
                            && arr.getNext().getOpcode() == ILOAD
                            && arr.getNext().getNext().getOpcode() == AALOAD
                            && arr.getNext().getNext().getNext().getOpcode() == ILOAD
                            && arr.getNext().getNext().getNext().getNext().getOpcode() == AALOAD
                            && arr.getNext().getNext().getNext().getNext().getNext().getOpcode() == ALOAD
                            && arr.getNext().getNext().getNext().getNext().getNext().getNext().getOpcode() == INVOKEVIRTUAL) {
                        int x = ((VarInsnNode) arr.getNext()).var;
                        int y = ((VarInsnNode) arr.getNext().getNext().getNext()).var;
                        int pickable = ((VarInsnNode) arr.getNext().getNext().getNext().getNext().getNext()).var;


                        InsnList code = new InsnList();

                        // item.x = sceneX >> 7 + 64
                        code.add(new VarInsnNode(ALOAD, pickable));
                        code.add(new VarInsnNode(ILOAD, x));
                        code.add(new IntInsnNode(BIPUSH, 7));
                        code.add(new InsnNode(ISHL));
                        code.add(new IntInsnNode(BIPUSH, 64));
                        code.add(new InsnNode(IADD));
                        code.add(new FieldInsnNode(PUTFIELD, item, "sceneX", "I"));

                        // item.y = sceneY >> 7 + 64
                        code.add(new VarInsnNode(ALOAD, pickable));
                        code.add(new VarInsnNode(ILOAD, y));
                        code.add(new IntInsnNode(BIPUSH, 7));
                        code.add(new InsnNode(ISHL));
                        code.add(new IntInsnNode(BIPUSH, 64));
                        code.add(new InsnNode(IADD));
                        code.add(new FieldInsnNode(PUTFIELD, item, "sceneY", "I"));

                        code.add(new VarInsnNode(ALOAD, pickable));
                        code.add(new LdcInsnNode(level.getMultiplier()));
                        code.add(new FieldInsnNode(GETSTATIC, level.getOwner(), level.getInternalName(), "I"));
                        code.add(new InsnNode(IMUL));
                        code.add(new FieldInsnNode(PUTFIELD, item, "floorLevel", "I"));

                        mn.instructions.insert(arr.getNext().getNext().getNext().getNext().getNext().getNext(), code);
                        break;
                    }
                }
            }
        }
    }
}
