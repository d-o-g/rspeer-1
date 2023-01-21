package org.rspeer.networking.acuity.services.path_finding.domain;


import org.rspeer.networking.acuity.services.player_cache.domain.CachedPlayer;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaLocation;

import java.util.List;

public class PathRequest {

    private List<HpaLocation> start, end;
    private CachedPlayer player;

    public List<HpaLocation> getStart() {
        return start;
    }

    public PathRequest setStart(List<HpaLocation> start) {
        this.start = start;
        return this;
    }

    public List<HpaLocation> getEnd() {
        return end;
    }

    public PathRequest setEnd(List<HpaLocation> end) {
        this.end = end;
        return this;
    }

    public CachedPlayer getPlayer() {
        return player;
    }

    public void setPlayer(CachedPlayer player) {
        this.player = player;
    }
}
