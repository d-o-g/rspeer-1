package org.objectweb.asm.commons.cfg;

import java.util.*;

/**
 * Created by tobyreynolds98 on 08/07/2017.
 *
 * https://en.wikipedia.org/wiki/Dominator_(graph_theory)
 */
public class Dominatrix {

    private final Map<Block, Block> immediateDominators;
    private final List<Block> blocks;
    private final List<Block> visited;

    public Dominatrix(List<Block> blocks) {
        this.blocks = blocks;
        immediateDominators = new HashMap<>();
        visited = new LinkedList<>();
        computeDominance();
    }

    private void computeDominance() {
        /*
        TODO
        The dominance frontier of a node d is the set of all nodes n such that d dominates
        an immediate predecessor of n, but d does not strictly dominate n.
        It is the set of nodes where d's dominance stops.
         */
    }

    //does dog dominate mad?
    public boolean dominates(Block dog, Block mad) {
        Block current = mad;
        while (current != null) {
            if (current == dog) {
                return true;
            }
            current = current.next; //immediateDominators.get(current);
        }
        return false;
    }

    public Block findCommonDominator(Block a, Block b) {
        Set<Block> path = new LinkedHashSet<>();

        Block a_traverse = a;
        Block b_traverse = b;

        while (a_traverse != null && path.add(a_traverse)) {
            a_traverse = a_traverse.next;
        }

        while (b_traverse != null) {
            if (path.contains(b_traverse)) {
                return b_traverse;
            }
            b_traverse = b_traverse.next;
        }

        return null;
    }
}