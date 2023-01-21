package org.rspeer.injector.adapter.hook;

import org.objectweb.asm.tree.*;
import org.rspeer.injector.Modscript;
import org.rspeer.injector.adapter.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.runetek.providers.RSNpc;
import org.rspeer.runetek.providers.RSPathingEntity;
import org.rspeer.runetek.providers.RSPlayer;

import java.util.Map;

public final class PathingEntityIndexAdapter extends CodeAdapter {

    private static final int[] PATTERN = {
            GETSTATIC, ILOAD, NEW, DUP, INVOKESPECIAL, AASTORE
    };

    private final ClassHook player, npc, entity;

    public PathingEntityIndexAdapter(Modscript modscript, Map<String, ClassNode> library) {
        super(modscript, library);
        player = modscript.resolve(RSPlayer.class);
        npc = modscript.resolve(RSNpc.class);
        entity = modscript.resolve(RSPathingEntity.class);
    }

    private static boolean match(AbstractInsnNode ain, int... pattern) {
        for (int op : pattern) {
            if (ain == null || op != ain.getOpcode()) {
                return false;
            }
            ain = ain.getNext();
        }
        return true;
    }

    @Override
    public void visitClassNode(ClassNode cn) {

    }

    @Override
    public void visitMethodNode(MethodNode mn) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (match(ain, PATTERN)) {
                TypeInsnNode tin = (TypeInsnNode) ain.getNext().getNext();
                if (tin.desc.equals(npc.getInternalName())) {
                    FieldInsnNode cache = (FieldInsnNode) ain;
                    int var = ((VarInsnNode) ain.getNext()).var;
                    //mn.instructions.insert(match[4], new InsnNode( UP_X2));
                    InsnList stack = new InsnList();

                    stack.add(new FieldInsnNode(GETSTATIC, cache.owner, cache.name, cache.desc));
                    stack.add(new VarInsnNode(ILOAD, var));
                    stack.add(new InsnNode(AALOAD));
                    stack.add(new VarInsnNode(ILOAD, var));
                    stack.add(new FieldInsnNode(PUTFIELD, entity.getInternalName(), "index", "I"));

                    mn.instructions.insert(ain.getNext().getNext().getNext().getNext().getNext(), stack); //haha

                    //TODO not complete for player
                }
            }
        }
    }
}
