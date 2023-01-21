package org.rspeer.script.tree;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.script.Script;

/**
 * Class defining a TreeScript structure. Explores the implemented binary tree for current state and performed action.
 *
 * @author Jasper
 */
public abstract class TreeScript extends Script {

    /**
     * Root of the TreeScript
     */
    public abstract TreeNode getRoot();

    /**
     * Looping method to start at the root
     */
    @Override
    public final int loop() {
        evaluate(getRoot());
        return 50;
    }

    /**
     * Recursive method to traverse the binary tree structure
     *
     * @param node being evaluated
     */
    private void evaluate(TreeNode node) {
        if (node instanceof TreeLeaf) {
            TreeLeaf leaf = (TreeLeaf) node;
            for (int i = 0; i < leaf.repeat() && !leaf.traverse(); i++) {
                int sleep = leaf.sleep();
                if (sleep == -1) {
                    setStopping(true);
                    return;
                }
                Time.sleep(sleep);
            }
        } else {
            if (node.traverse()) {
                evaluate(node.getRight());
            } else {
                evaluate(node.getLeft());
            }
        }
    }
}