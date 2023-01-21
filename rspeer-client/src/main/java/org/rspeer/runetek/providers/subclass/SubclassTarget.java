package org.rspeer.runetek.providers.subclass;

import org.rspeer.runetek.providers.RSCanvas;
import org.rspeer.runetek.providers.RSProvider;

public enum SubclassTarget {

    CANVAS(RSCanvas.class, GameCanvas.class);

    private final Class<? extends RSProvider> providerType;
    private final Class<?> superType;

    SubclassTarget(Class<? extends RSProvider> providerType, Class<?> superType) {
        this.providerType = providerType;
        this.superType = superType;
    }

    public Class<? extends RSProvider> getProviderType() {
        return providerType;
    }

    public String getDefinedName() {
        return providerType.getSimpleName().replace("RS", "");
    }

    public Class<?> getSuperType() {
        return superType;
    }

    public String getSuperName() {
        return superType.getSimpleName();
    }
}
