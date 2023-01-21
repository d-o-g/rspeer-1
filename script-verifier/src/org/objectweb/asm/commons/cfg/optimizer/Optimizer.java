package org.objectweb.asm.commons.cfg.optimizer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tobyreynolds98 on 07/07/2017.
 */
public class Optimizer implements Opcodes {

    private static int empties = 0, addedGotos = 0, merged = 0;

    public static List<Block> flatten(MethodNode mn, List<Block> blocks) {
        blocks = new ArrayList<>(blocks);
      //  removeEmpties(blocks);
    //    reduceNesting(blocks);
      //  saveToMethod(mn, blocks, null);
        return blocks;
    }

    private static void reduceNesting(List<Block> blocks) {
        List<Block> remove = new ArrayList<>();
        for (Block block : blocks) {
            if (!block.isConditionalFlow()) {
                continue;
            }
            Block trueBranch = block.getTrueBranch();
            Block falseBranch = block.getFalseBranch();
            boolean trueExits = trueBranch != null && trueBranch.isUnconditionalFlow();
            boolean falseExits = falseBranch != null && falseBranch.isUnconditionalFlow();
            if (trueExits) {
                remove.add(merge(block, falseBranch));
            } else if (falseExits) {
                remove.add(merge(block, trueBranch));
            }
        }
        merged += remove.size();
        blocks.removeAll(remove);
    }

    private static void saveToMethod(MethodNode mn, List<Block> blocks, Comparator<Block> order) {
        mn.instructions.removeAll(true);
        if (order != null) {
            blocks.sort(order);
        } else {
           // blocks.sort(Comparator.comparingInt(b -> mn.instructions.indexOf(new LabelNode(b.label))));
        }

        for (Block block : blocks) {
            block.instructions.forEach(ain -> mn.instructions.add(ain));
        }
    }

    private static Block merge(Block b, Block toMerge) {
        b.instructions.remove(b.getLast()); //remove goto
        toMerge.instructions.remove(toMerge.getFirst()); //remove label
        b.instructions.addAll(toMerge.instructions);
        return toMerge;
    }

    private static void removeEmpties(List<Block> blocks) {
        List<Block> empty = new ArrayList<>();
        for (Block block : blocks) {
            if (block.isEmpty() && block.preds.size() == 0) {
                empty.add(block);
                empties++;
            }
        }
        blocks.removeAll(empty);
    }

    public static String debug() {
        StringBuilder sb = new StringBuilder();
        /*sb.append("\t").append("Removed ").append(empties).append(" empty basic blocks");
        sb.append("\n");
        sb.append("\t").append("Inserted ").append(addedGotos).append(" GOTO instructions");
        sb.append("\n");
        sb.append("\t").append("Merged ").append(merged).append(" basic blocks");
        sb.append("\n");*/
        return sb.toString();
    }
}