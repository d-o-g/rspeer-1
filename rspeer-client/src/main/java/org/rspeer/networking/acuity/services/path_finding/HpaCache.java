package org.rspeer.networking.acuity.services.path_finding;

import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.networking.acuity.services.player_cache.PlayerCacheService;
import org.rspeer.runetek.api.movement.path.HpaPath;
import org.rspeer.runetek.api.movement.pathfinding.hpa.cache.AbstractHpaCache;
import org.rspeer.runetek.api.movement.position.Position;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zachary Herridge on 7/11/2018.
 */
public class HpaCache extends AbstractHpaCache {

    private Map<Position, CacheEntry> pathCache = new ConcurrentHashMap<>();
    private boolean cacheEnabled = true;
    private long maxCacheTime = TimeUnit.SECONDS.toMillis(45);

    public HpaCache() {
        RsPeerExecutor.scheduleAtFixedRate(this::cleanCache, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public HpaPath getPath(Position source, Position destination) {
        if (cacheEnabled) {
            CacheEntry cacheEntry = pathCache.get(destination);
            if (cacheEntry != null) {
                return cacheEntry.getHpaPath();
            }
        }

        HpaPath path = HpaService.findPath(source, destination, PlayerCacheService.INSTANCE.getCachedPlayer());
        if (cacheEnabled && path != null) {
            cache(new CacheEntry(destination, path, System.currentTimeMillis()));
        }
        return path;
    }

    @Override
    public void decache(HpaPath hpaPath) {
        if (hpaPath == null || hpaPath.getDestination() == null) {
            return;
        }
        pathCache.remove(hpaPath.getDestination());
    }

    @Override
    public void refresh(HpaPath hpaPath) {
        if (hpaPath == null || hpaPath.getDestination() == null) {
            return;
        }
        CacheEntry cacheEntry = pathCache.get(hpaPath.getDestination());
        if (cacheEntry != null) {
            cacheEntry.refresh();
        }
    }

    public void cache(CacheEntry cacheEntry) {
        cacheEntry.getHpaPath().setCache(this);
        pathCache.put(cacheEntry.getDestination(), cacheEntry);
    }

    public void cleanCache() {
        long now = System.currentTimeMillis();
        for (CacheEntry cacheEntry : pathCache.values()) {
            if ((now - cacheEntry.getEntryTime()) > maxCacheTime) {
                pathCache.remove(cacheEntry.getDestination());
            }
        }
    }

    public void clearCache() {
        pathCache.clear();
    }

    public HpaCache setMaxCacheTime(long maxCacheTime) {
        this.maxCacheTime = maxCacheTime;
        return this;
    }

    public class CacheEntry {

        private Position destination;
        private HpaPath hpaPath;
        private long entryTime;

        public CacheEntry(Position destination, HpaPath hpaPath, long entryTime) {
            this.destination = destination;
            this.hpaPath = hpaPath;
            this.entryTime = entryTime;
        }

        public Position getDestination() {
            return destination;
        }

        public HpaPath getHpaPath() {
            return hpaPath;
        }

        public long getEntryTime() {
            return entryTime;
        }


        public void refresh() {
            entryTime = System.currentTimeMillis();
        }
    }
}
