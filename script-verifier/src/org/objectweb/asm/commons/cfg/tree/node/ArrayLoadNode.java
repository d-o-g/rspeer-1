package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by tobyreynolds98 on 27/07/2017.
 */
public class ArrayLoadNode extends AbstractNode {

    public ArrayLoadNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    /**
     * @return The index of the array to load from
     */
    public AbstractNode getIndex() {
        return child(1);
    }

    /**
     * @return The array
     */
    public AbstractNode getArray() {
        return child(0);
    }
}
