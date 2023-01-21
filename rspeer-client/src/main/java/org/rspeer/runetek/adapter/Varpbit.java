package org.rspeer.runetek.adapter;

import org.rspeer.runetek.providers.RSDoublyNode;
import org.rspeer.runetek.providers.RSNode;
import org.rspeer.runetek.providers.RSVarpbit;

public final class Varpbit extends Adapter<RSVarpbit, Varpbit> implements RSVarpbit {

    private final int id;

    public Varpbit(RSVarpbit raw, int id) {
        super(raw);
        this.id = id;
    }

    @Override
    public int getVarpIndex() {
        return provider.getVarpIndex();
    }

    @Override
    public int getLower() {
        return provider.getLower();
    }

    @Override
    public int getUpper() {
        return provider.getUpper();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("(Bit %d)<Varp %d> ( %d -> %d | %d ) == %d", getId(), getVarpIndex(), getLower(), getUpper(), getMask(), getValue());
    }

    public boolean booleanValue() {
        return getValue() == 1;
    }

    @Override
    public long getKey() {
        return provider.getKey();
    }

    @Override
    public RSNode getNext() {
        return provider.getNext();
    }

    @Override
    public RSNode getPrevious() {
        return provider.getPrevious();
    }

    @Override
    public RSDoublyNode getNextDoubly() {
        return provider.getNextDoubly();
    }

    @Override
    public RSDoublyNode getPreviousDoubly() {
        return provider.getPreviousDoubly();
    }
}
