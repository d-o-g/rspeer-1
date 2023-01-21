package org.rspeer.injector.api;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.rspeer.runetek.api.commons.predicate.Predicates;

import java.util.Iterator;
import java.util.function.Predicate;

public class InsnIterator implements Iterator<AbstractInsnNode>, Iterable<AbstractInsnNode> {

    private AbstractInsnNode ptr;

    public InsnIterator(AbstractInsnNode ptr) {
        this.ptr = ptr;
    }

    @Override
    public boolean hasNext() {
        return ptr.getNext() != null;
    }

    @Override
    public AbstractInsnNode next() {
        ptr = ptr.getNext();
        return ptr;
    }

    public <T extends AbstractInsnNode> T nextOf(Class<T> type) {
       return nextOf(type, Predicates.always());
    }

    public <T extends AbstractInsnNode> T nextOf(Class<T> type, Predicate<T> test) {
        while (hasNext()) {
            AbstractInsnNode node = next();
            if (type.isInstance(node)) {
                T cast = type.cast(ptr);
                if (test.test(cast)) {
                    return cast;
                }
            }
        }

        return null;
    }

    @Override
    public Iterator<AbstractInsnNode> iterator() {
        return this;
    }
}
