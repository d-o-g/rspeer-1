package org.rspeer.runetek.providers;

import org.rspeer.runetek.adapter.scene.Npc;

public interface RSNpc extends RSPathingEntity {

    RSNpcDefinition getDefinition();

    Npc getWrapper();
}