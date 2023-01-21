package org.rspeer.runetek.providers;

import java.util.ArrayList;
import java.util.List;

public interface RSRS3CopiedNodeTable<T extends RSNode> extends RSProvider {

    T lookup(long l);

    int getIndex();

    int getSize();

    RSNode getHead();

    RSNode getTail();

    RSNode[] getBuckets();

    default T safeLookup(long key) {
        try {
            RSNode node = getBuckets()[(int) (key & (long) (getSize() - 1))];
            for (RSNode head = node.getNext(); head != node; head = head.getNext()) {
                if (head.getKey() == key) {
                    return (T) head;
                }
            }
        } catch (Exception ignored) {

        }
        return null;
    }

    default List<T> toList() {
        List<T> nodes = new ArrayList<>();
        RSNode[] buckets = getBuckets();
        for (RSNode sentinel : buckets) {
            RSNode cur = sentinel.getNext();
            while (cur != sentinel) {
                nodes.add((T) cur);
                cur = cur.getNext();
            }
        }
        return nodes;
    }
}