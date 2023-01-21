package org.rspeer.injector.adapter.wrapper;

import org.rspeer.runetek.adapter.Adapter;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.Projectile;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.providers.*;

public enum InjectedWrapper {

    INTERFACE_COMPONENT(RSInterfaceComponent.class, InterfaceComponent.class),
    NPC(RSNpc.class, Npc.class),
    PLAYER(RSPlayer.class, Player.class),
    // PICKABLE(RSPickable.class, Pickable.class),
    PROJECTILE(RSProjectile.class, Projectile.class),
    BOUNDARY_DECOR(RSBoundaryDecor.class, SceneObject.class),
    BOUNDARY(RSBoundary.class, SceneObject.class),
    ENTITY_MARKER(RSEntityMarker.class, SceneObject.class),
    TILE_DECOR(RSTileDecor.class, SceneObject.class);

    private final Class<? extends RSProvider> peerClass;
    private final Class<? extends Adapter> adapterClass;

    InjectedWrapper(Class<? extends RSProvider> peerClass, Class<? extends Adapter> adapterClass) {
        this.peerClass = peerClass;
        this.adapterClass = adapterClass;
    }

    public String getAdapterName() {
        return adapterClass.getName().replace('.', '/');
    }

    public String getPeerName() {
        return peerClass.getName().replace('.', '/');
    }

    public Class<? extends RSProvider> getPeerClass() {
        return peerClass;
    }

    public Class<? extends Adapter> getAdapterClass() {
        return adapterClass;
    }

    @Override
    public String toString() {
        String name = super.name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace('_', ' ');
    }
}
