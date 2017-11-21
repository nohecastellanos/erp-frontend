package com.jukusoft.erp.gui.cache.impl;

import com.jukusoft.erp.gui.cache.CacheManager;
import com.jukusoft.erp.gui.cache.CacheTypes;
import com.jukusoft.erp.gui.cache.ICache;
import com.jukusoft.erp.gui.logging.ILogging;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCacheManager implements CacheManager {

    //local cache directory
    protected File localCacheDir = null;

    //instance of logger
    protected ILogging logger = null;

    //map with all cache instances
    protected Map<String,ICache> cacheMap = new ConcurrentHashMap<>();

    public DefaultCacheManager (File localCacheDir, ILogging logger) {
        this.localCacheDir = localCacheDir;
        this.logger = logger;
    }

    @Override
    public ICache getCache(String cacheName) {
        return this.cacheMap.get(cacheName);
    }

    @Override
    public boolean containsCache(String cacheName) {
        return this.getCache(cacheName) != null;
    }

    @Override
    public ICache createCache(String cacheName, CacheTypes type) {
        //first check, if cache already exists
        if (this.getCache(cacheName) != null) {
            throw new IllegalStateException("cache '" + cacheName + "' does already exists.");
        }

        ICache cache = null;

        switch (type) {
            case FILE_CACHE:
                cache = new FileSystemCache(localCacheDir.getAbsolutePath() + "/" + cacheName.toLowerCase() + "/", cacheName, this.logger);
                break;

            case LOCAL_MEMORY_CACHE:
                cache = new LocalMemoryCache(this.logger);
                break;

            case HAZELCAST_CACHE:
                throw new UnsupportedOperationException("hazelcast cache isnt supported on client side.");

            default:
                throw new IllegalArgumentException("Unknown cache type: " + type);
        }

        this.cacheMap.put(cacheName, cache);

        return cache;
    }

    @Override
    public void removeCache(String cacheName) {
        ICache cache = this.getCache(cacheName);

        //remove cache from map
        this.cacheMap.remove(cacheName);

        //cleanUp cache if neccessary
        if (cache != null) {
            cache.cleanUp();
        }
    }

    @Override
    public void cleanUp() {
        for (Map.Entry<String,ICache> entry : this.cacheMap.entrySet()) {
            entry.getValue().cleanUp();
        }
    }

}
