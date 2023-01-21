package org.rspeer.commons;

import java.io.IOException;
import java.net.Socket;

public abstract class SocketProvider {

    protected String host;
    protected int port;

    public final void init(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public abstract Socket provide() throws IOException;

    public Socket direct() throws IOException {
        return new Socket(host, port);
    }
}
