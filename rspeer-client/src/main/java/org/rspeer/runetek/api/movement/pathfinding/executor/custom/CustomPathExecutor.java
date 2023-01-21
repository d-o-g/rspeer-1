package org.rspeer.runetek.api.movement.pathfinding.executor.custom;

import com.allatori.annotations.DoNotRename;
import org.rspeer.networking.acuity.services.path_finding.HpaService;
import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.Path;
import org.rspeer.runetek.api.movement.pathfinding.executor.custom.paths.LumbridgePub;

import java.util.concurrent.CopyOnWriteArrayList;

@DoNotRename
public class CustomPathExecutor {

    private CopyOnWriteArrayList<CustomPath> paths;
    private CustomPath currentCustomPath;

    public CustomPathExecutor() {
        this.paths = new CopyOnWriteArrayList<>();
        this.paths.add(new LumbridgePub());
    }

    @DoNotRename
    public void remove(CustomPath path) {
        for (CustomPath customPath : paths) {
            if (customPath.equals(path) || customPath.getName().equals(path.getName())) {
                paths.remove(path);
                break;
            }
        }
    }

    @DoNotRename
    public void add(CustomPath path) {
        paths.add(path);
    }

    @DoNotRename
    public CopyOnWriteArrayList<CustomPath> getPaths() {
        return paths;
    }

    @DoNotRename
    public CustomPath getCurrentCustomPath() {
        return currentCustomPath;
    }

    @DoNotRename
    public Path execute(Positionable start, Positionable end) {
        CustomPath temp = null;
        for (CustomPath customPath : paths) {
            if (!customPath.validate(start, end)) {
                continue;
            }
            temp = customPath;
            break;
        }
        if(temp != null) {
            currentCustomPath = temp;
            return Movement.buildPathInternal(start, currentCustomPath.getDestination());
        }
        if(currentCustomPath != null) {
            currentCustomPath = null;
            HpaService.getHpaCache().clearCache();
        }
        return null;
    }
}
