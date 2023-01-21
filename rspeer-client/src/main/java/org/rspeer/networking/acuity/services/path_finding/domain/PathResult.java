package org.rspeer.networking.acuity.services.path_finding.domain;


import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaEdge;
import org.rspeer.runetek.api.movement.pathfinding.hpa.graph.HpaLocation;

import java.util.List;
import java.util.Map;

public class PathResult {

    private String error;
    private List<HpaEdge> path;
    private Map<String, List<HpaLocation>> subPaths;

    public String getError() {
        return error;
    }

    public List<HpaEdge> getPath() {
        return path;
    }

    public Map<String, List<HpaLocation>> getSubPaths() {
        return subPaths;
    }
}
