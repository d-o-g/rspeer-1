package org.rspeer.runetek.api.commons;

import java.util.stream.IntStream;

public final class Streams {

    private Streams() {
        throw new IllegalAccessError();
    }

    public static IntStream intRange(int endExclusive) {
        return IntStream.range(0, endExclusive);
    }
}
