package org.objectweb.asm.commons.cfg.tree.node.expr;

import org.objectweb.asm.commons.cfg.Block;

/**
 * Created by tobyreynolds98 on 08/07/2017.
 */
public class LoopExpr {

    private final Type type;
    private final Expr condition;
    private final Block body;

    public LoopExpr(Type type, Expr condition, Block body) {
        this.type = type;
        this.condition = condition;
        this.body = body;
    }

    public Type getType() {
        return type;
    }

    public Expr getCondition() {
        return condition;
    }

    public Block getBody() {
        return body;
    }

    public enum Type {
        PRE_CONDITION, //do while
        POST_CONDITION
    }
}
