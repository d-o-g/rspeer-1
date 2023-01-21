package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by tobyreynolds98 on 27/07/2017.
 */
public class ReturnNode extends AbstractNode {

    public ReturnNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    /**
     * @return The return value
     */
    public AbstractNode getValue() {
        return opcode() == RETURN ? null : child(0);
    }
}
