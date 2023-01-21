package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by tobyreynolds98 on 27/07/2017.
 */
public class ArrayStoreNode extends AbstractNode {

    public ArrayStoreNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    /**
     * @return The array that the value is being stored into
     */
    public AbstractNode getArray() {
        return child(0);
    }

    /**
     * @return The array index in which the value is being stored to
     */
    public AbstractNode getIndex() {
        return child(1);
    }

    /**
     * @return The value being stored into the array
     */
    public AbstractNode getValue() {
        return child(2);
    }
}
