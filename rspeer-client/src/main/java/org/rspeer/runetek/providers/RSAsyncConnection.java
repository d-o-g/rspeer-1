package org.rspeer.runetek.providers;

import java.net.Socket;

public interface RSAsyncConnection extends RSConnection {

    void finalize();

    Socket getSocket();

    RSAsyncInputStream getInput();

    RSAsyncOutputStream getOutput();
}
