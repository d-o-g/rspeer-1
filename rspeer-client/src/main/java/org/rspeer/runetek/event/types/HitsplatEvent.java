package org.rspeer.runetek.event.types;

import org.rspeer.runetek.adapter.scene.PathingEntity;
import org.rspeer.runetek.event.Event;
import org.rspeer.runetek.event.EventListener;
import org.rspeer.runetek.event.listeners.HitsplatListener;

/**
 * Created by Spencer on 31/03/2018.
 */
public final class HitsplatEvent extends Event<PathingEntity> {

    /**
     * Blue hitsplat - 0 damage
     */
    public static final int TYPE_NO_DAMAGE = 0;

    /**
     * Red hitsplat - Standard damage
     */
    public static final int TYPE_STANDARD_DAMAGE = 1;

    /**
     * Green hitsplat - poison damage
     */
    public static final int TYPE_POISON_DAMAGE = 2;

    /**
     * Orange hitsplat - Disease damage
     */
    public static final int TYPE_DISEASE_DAMAGE = 4;

    /**
     * Teal hitsplat - Venom damage
     */
    public static final int TYPE_VENOM_DAMAGE = 5;

    /**
     * Purple hitsplat - heal
     */
    public static final int TYPE_HEAL = 6;

    private final int type, damage, id;

    public HitsplatEvent(PathingEntity source, int type, int damage, int id) {
        super(source);
        this.type = type;
        this.damage = damage;
        this.id = id;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof HitsplatListener) {
            ((HitsplatListener) listener).notify(this);
        }
    }

    public int getType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public int getId() {
        return id;
    }
}
