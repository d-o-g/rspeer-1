package org.rspeer.injector.hook;

public abstract class Hook {

    private final String definedName;

    public Hook(String definedName) {
        this.definedName = definedName;
    }

    public String getDefinedName() {
        return definedName;
    }
}
