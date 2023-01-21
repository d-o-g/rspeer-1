package org.objectweb.asm.commons.cfg.tree.node.expr;

import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;

import java.util.List;

/**
 * Created by tobyreynolds98 on 08/07/2017.
 */
public abstract class Expr {

    protected final List<AbstractNode> children;

    public Expr(List<AbstractNode> children) {
        this.children = children;
    }
}
