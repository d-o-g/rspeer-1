package org.rspeer.runetek.providers;

public interface RSScriptEvent extends RSNode {

    RSInterfaceComponent getSource();

    Object[] getArgs();

    void setArgs(Object[] args);

    default int getScriptId() {
        Object[] args = getArgs();
        return args != null && args.length > 0 && args[0] instanceof Integer ? (int) args[0] : -1;
    }
}