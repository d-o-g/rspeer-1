package org.rspeer.script.tree;

import org.rspeer.runetek.api.commons.math.Random;

/**
 * A class describing a leaf node, which is the outermost node of a tree and should not be evaluated
 * for its children nodes but should be evaluated for its action success; which is why it does not
 * implement the {@link #traverse()} method. Overridden {@link #getLeft()} and {@link #getRight()} methods are final
 * to prevent misuse.
 *
 * @author Jasper
 */
public abstract class TreeLeaf extends TreeNode {

    /**
     * A Leaf node should never be able to have a left node
     */
    @Override
    public final TreeNode getLeft() {
        return null;
    }

    /**
     * A Leaf node should never be able to have a right node
     */
    @Override
    public final TreeNode getRight() {
        return null;
    }

    /**
     * Integer defining how many times this should be attempted again before restarting tree evaluation.
     * Relies on correct usage of {@link #traverse()}
     *
     * @return the amount of times this action is to be attempted before failure is concluded; default set to 1.
     */
    public int repeat() {
        return 1;
    }

    /**
     * The length this action should be sleeping before attempting again (Is not executed upon the success iteration).
     *
     * @return sleeping time.
     */
    public int sleep() {
        return Random.low(50, 200);
    }
}