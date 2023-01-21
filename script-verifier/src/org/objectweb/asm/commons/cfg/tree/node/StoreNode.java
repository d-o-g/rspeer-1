package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by tobyreynolds98 on 27/07/2017.
 */
public class StoreNode extends VariableNode {

    public StoreNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    /**
     * @return The value being stored
     */
    public AbstractNode getValue() {
        return child(0);
    }
}
