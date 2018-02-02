package com.test.weathermusic.util.bean;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Created by Eduardo on 31/01/2018.
 */
@Component
public class CacheHelper {

    private CacheManager cacheManager;

    public CacheHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public <E> E get(String cacheName, Object key, Class<E> clazz) {
        return cacheManager.getCache(cacheName).get(key, clazz);
    }
}
