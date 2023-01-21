package org.rspeer.script.tree;

/**
 * A class describing a node. Contains three abstract methods that are implemented to define the type of node.
 * A helper method for parent is in here but not being used as that is up to implementation whether or not the
 * user wants to be able to evaluate the parent node. Subclass {@link TreeLeaf} should be used to define a leaf
 * and not an implementation of this class because type evaluation is done using instance checks and not null checks.
 *
 * @author Jasper
 */
public abstract class TreeNode {

    /**
     * Possibility to save parent nodes, default off; override to implement
     *
     * @return the parent node
     */
    public TreeNode getParent() {
        return null;
    }

    /**
     * The left node of this node, this node will be traversed if {@link #traverse()} returns false.
     */
    public abstract TreeNode getLeft();

    /**
     * The right node of this node, this node will be traversed if {@link #traverse()} returns true.
     */
    public abstract TreeNode getRight();

    /**
     * Traversal method to evaluate which node should be traversed next.
     * If this node is an instance of {@link TreeLeaf} this method should return true if the
     * to be performed action was successfully completed.
     */
    public abstract boolean traverse();
}