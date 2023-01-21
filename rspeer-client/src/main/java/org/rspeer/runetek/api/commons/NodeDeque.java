package org.rspeer.runetek.api.commons;

import org.rspeer.runetek.adapter.Adapter;
import org.rspeer.runetek.providers.RSNode;
import org.rspeer.runetek.providers.RSNodeDeque;

import java.util.Iterator;

/**
 * Created by Spencer on 18/02/2018.
 */
public final class NodeDeque extends Adapter<RSNodeDeque, NodeDeque> implements Iterable<RSNode> {

    private RSNode current;

    public NodeDeque(RSNodeDeque raw) {
        super(raw);
        if (raw.getTail() != null) {
            current = raw.getTail().getNext();
        }
    }

    public int getSize() {
        int size = 0;
        RSNode head = provider.getTail();
        RSNode node = head.getNext();
        while (node != head) {
            node = node.getNext();
            size++;
        }
        return size;
    }

    @Override
    public Iterator<RSNode> iterator() {
        return new Iterator<RSNode>() {
            @Override
            public boolean hasNext() {
                return current != null && current.getNext() != null;
            }

            @Override
            public RSNode next() {
                RSNode temp = current;
                if (temp == provider.getTail()) {
                    current = null;
                    return null;
                }
                current = temp.getNext();
                return temp;
            }
        };
    }
}
