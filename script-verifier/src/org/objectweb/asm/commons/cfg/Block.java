package org.objectweb.asm.commons.cfg;

import org.objectweb.asm.Label;
import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.asm.commons.query.InsnQuery;
import org.objectweb.asm.commons.util.Assembly;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Tyler Sedlar
 */
public class Block implements Comparable<Block> {

    public final Label label;
    public final List<AbstractInsnNode> instructions = new LinkedList<>();
    public final List<Block> preds = new ArrayList<>();
    public MethodNode owner = null;
    public Block next; //immediate successor
    public Block target; //the block that this flows into (the "fallthrough" block)

    public Stack<AbstractInsnNode> stack = new Stack<>();

    private NodeTree tree;

    private int index = -1;

    /**
     * Constructs a block for the given label.
     *
     * @param label The label in which to create a block from.
     */
    public Block(Label label) {
        this.label = label;
        this.instructions.add(new LabelNode(label));
    }

    /**
     * Constructs a NodeTree for the current block.
     */
    public NodeTree tree() {
        if (tree != null) return tree;
        return (tree = TreeBuilder.build(this));
    }

    /**
     * Sets this block's index.
     *
     * @param index The index to set.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Checks if the block is empty.
     *
     * @return true if the block is empty, otherwise false.
     */
    public boolean isEmpty() {
        return preds.isEmpty() && instructions.size() <= 1;
    }

    @Override
    public int compareTo(Block block) {
        return index > block.index ? 1 : -1;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int spacing) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < spacing; i++) {
            s.append("\t");
        }
        s.append("Block #").append(index).append("\r\n");
        for (AbstractInsnNode ain : instructions) {
            for (int i = 0; i < spacing; i++) {
                s.append("\t");
            }
            s.append(Assembly.toString(ain)).append("\r\n");
        }
        return s.toString();
    }

    /**
     * Gets the amount of times the given opcodes has been matched
     *
     * @param opcode The opcode to match
     * @return The amount of times the given opcode has been matched.
     */
    public int count(int opcode) {
        int count = 0;
        for (AbstractInsnNode ain : instructions) {
            if (ain.opcode() == opcode)
                count++;
        }
        return count;
    }

    /**
     * Gets the amount of times the given query has been matched
     *
     * @param query The query to match
     * @return The amount of times the given query has been matched.
     */
    public int count(InsnQuery query) {
        int count = 0;
        for (AbstractInsnNode ain : instructions) {
            if (query.matches(ain))
                count++;
        }
        return count;
    }

    /**
     * Gets the matched instruction at the given index
     *
     * @param opcode The opcode of the instruction to match
     * @param index  The index to match at
     * @return The matched instruction at the given index
     */
    public AbstractInsnNode get(int opcode, int index) {
        int i = 0;
        for (AbstractInsnNode ain : instructions) {
            if (ain.opcode() == opcode) {
                if (i == index)
                    return ain;
                i++;
            }
        }
        return null;
    }

    /**
     * Gets the first matched instruction
     *
     * @param opcode The opcode of the instruction to match
     * @return The first matched instruction
     */
    public AbstractInsnNode get(int opcode) {
        return get(opcode, 0);
    }

    /**
     * Gets the matched instruction at the given index
     *
     * @param query The query to match
     * @param index The index to match at
     * @return The matched instruction at the given index
     */
    public AbstractInsnNode get(InsnQuery query, int index) {
        int i = 0;
        for (AbstractInsnNode ain : instructions) {
            if (query.matches(ain)) {
                if (i == index)
                    return ain;
                i++;
            }
        }
        return null;
    }

    /**
     * Gets the first matched instruction
     *
     * @param query The query to match
     * @return The first matched instruction
     */
    public AbstractInsnNode get(InsnQuery query) {
        return get(query, 0);
    }

    public Block follow() {
        Block block = new Block(null);
        block.owner = owner;
        block.instructions.addAll(instructions);
        List<Block> followed = new ArrayList<>();
        Block next = this;
        followed.add(next);
        while (next.next != null) {
            next = next.next;
            if (followed.contains(next)) {
                break;
            }
            followed.add(next);
            for (AbstractInsnNode ain : next.instructions) {
                if (ain instanceof LabelNode) {
                    continue;
                }
                block.instructions.add(ain);
            }
        }
        return block;
    }

    public List<Block> computeSuccessors() {
        List<Block> blocks = new ArrayList<>();
        Block next = this;
        while (next.next != null) {
            next = next.next;
            if (blocks.contains(next)) {
                break;
            }
            blocks.add(next);
        }
        return blocks;
    }

    public Block getTrueBranch() {
        List<Block> succTheD = computeSuccessors();
        if (succTheD.size() == 1) {
            return succTheD.get(0);
        }
        AbstractInsnNode last = getLast();
        if (last instanceof JumpInsnNode) {
            LabelNode madIsACuck = ((JumpInsnNode) last).label;
            for (Block suckysucky : succTheD) {
                AbstractInsnNode first = suckysucky.getFirst();
                if (first != null && madIsACuck == first) {
                    return suckysucky;
                }
            }
        }
        return null;
    }

    public Block getFalseBranch() {
        List<Block> succTheD = computeSuccessors();
        AbstractInsnNode last = getLast();
        if (last instanceof JumpInsnNode) {
            LabelNode madIsACuck = ((JumpInsnNode) last).label;
            for (Block suckysucky : succTheD) {
                AbstractInsnNode first = suckysucky.getFirst();
                if (madIsACuck != first) {
                    return suckysucky;
                }
            }
        }
        return null;
    }

    public boolean isConditionalFlow() {
        return isBranch() && !isUnconditionalFlow();
    }

    public boolean isUnconditionalFlow() {
        int op = getLast().opcode();
        return (op >= GOTO && op <= RETURN) || op == ATHROW;
    }

    public boolean isBranch() {
        int op = getLast().opcode();
        return (op >= IFEQ && op <= RETURN) || op == ATHROW;
    }

    public void traversePreOrder(Function<Block, Iterable<Block>> children,
                                 Consumer<Block> visitAction,
                                 AtomicBoolean visited) {
        if (visited.get()) {
            return;
        }
        visited.set(true);
        visitAction.accept(this);
        for (Block child : children.apply(this)) {
            child.traversePreOrder(children, visitAction, new AtomicBoolean());
        }
    }

    public void traversePostOrder(Function<Block, Iterable<Block>> children,
                                  Consumer<Block> visitAction,
                                  AtomicBoolean visited) {
        if (visited.get()) {
            return;
        }
        visited.set(true);
        for (Block child : children.apply(this)) {
            child.traversePostOrder(children, visitAction, new AtomicBoolean());
        }
        visitAction.accept(this);
    }

    public AbstractInsnNode getFirst() {
        return instructions.size() > 0 ? instructions.get(0) : null;
    }

    public AbstractInsnNode getLast() {
        return instructions.size() > 0 ? instructions.get(instructions.size() - 1) : null;
    }
}
