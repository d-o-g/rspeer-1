package org.rspeer.runetek.api;

import org.rspeer.runetek.providers.RSClient;

/**
 * Created by Zachary Herridge on 3/6/2018.
 */
public final class ClientSupplier {

    private static RSClient instance;

    private ClientSupplier() {
        throw new IllegalAccessError();
    }

    public static RSClient get() {
        return instance;
    }

    public static void set(RSClient supplier) {
        ClientSupplier.instance = supplier;
    }
}
