package org.rspeer.injector.hook;

/**
 * Created by Spencer on 27/01/2018.
 */
public final class Multiplier {

    private final long decoder;
    private final String owner;
    private final String name;

    public Multiplier(long decoder, String owner, String name) {
        this.decoder = decoder;
        this.owner = owner;
        this.name = name;
    }

    public long getDecoder() {
        return decoder;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }
}
