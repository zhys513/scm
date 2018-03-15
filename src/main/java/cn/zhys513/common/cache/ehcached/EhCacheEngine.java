/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache.ehcached;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import cn.zhys513.common.cache.CacheConfig;
import cn.zhys513.common.cache.CacheEngine;

/**
 * <p>EhCacheEngine引擎<p/> 
 * 
 * @author zhys(13960826213@139.com)
 * @created 2012-4-18
 */
//@Component("ehCache")
public class EhCacheEngine  extends CacheConfig implements CacheEngine {

    private static final Logger log = Logger.getLogger(EhCacheEngine.class);

    protected Cache cache;
    
    protected static CacheManager manager = CacheManager.getInstance();


    /**
     * 构造方法
     */
    public EhCacheEngine() {
        this.cache = getCache("applicationCache");
    }

    /**
     * 构造方法
     * 
     * @param cacheName
     *            缓存的名称
     */
    public EhCacheEngine(String cacheName) {
        this.cache = getCache(cacheName);
    }

    public void stop() {
        manager.shutdown();
    }

    protected Cache getCache(String name) {
        if (!manager.cacheExists(name)) {
            synchronized (manager) {// 此处采用锁，因为可能初次访问一个cache时候都还未创建同时创建至于文件被锁,此处为了解决这个
                if (!manager.cacheExists(name)) {
                    try {
                        manager.addCache(name);
                    } catch (CacheException ce) {
                        log.error("EhCacheEngine.getCache", ce);
                    }
                }
            }

        }
        return manager.getCache(name);
    }

    public void add(String key, Object value) {
        if (log.isDebugEnabled()) {
            log.debug("Caching " + value + " with key " + key);
        }
        Element element = new Element(key, value);
        cache.put(element);
    }

    public void add(String cacheName, String key, Object value) {
        cache = getCache(cacheName);
        Element element = new Element(key, (Serializable) value);

        cache.put(element);
    }

    public Object get(String cacheName, String key) {
        cache = getCache(cacheName);
        Element element = cache.get(key);
        if (element == null) {
            return null;
        }
        return element.getObjectValue();
    }

    public Object get(String key) {
        Element element = cache.get(key);
        if (element == null) {
            return null;
        }
        return element.getObjectValue();
    }

    public void remove(String cacheName, String key) {
        cache = getCache(cacheName);
        cache.remove(key);
    }

    public void remove(String cacheName) {
        if (manager.cacheExists(cacheName)) {
            manager.removeCache(cacheName);
        }
    }

}
