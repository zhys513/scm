/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache.ehcached;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import cn.zhys513.common.cache.CacheProvider;
import cn.zhys513.common.cache.annotation.ReadThroughCache;
import net.sf.ehcache.Element;

/**
 * EhCachedProvider
 * 
 * @Component
 * 
 * @author zhys513
 */
public class EhCachedProvider extends EhCacheEngine implements CacheProvider {

    private static final Logger logger = Logger.getLogger(EhCachedProvider.class);

    private static final int DEF_EXPIRATION = 30 * 24 * 60 * 60; // 30天

    @Override
    public synchronized void set(String key, Object value, int expiration) {
        logger.debug(String.format("EhCachedProvider.set: key = %s value = %s expr = %d", key, value.toString(), expiration));
        try {
            cache.remove(key);
            Element element = new Element(key, value);  
            element.setTimeToLive(0);
            element.setTimeToIdle(expiration);
            cache.put(element);
        } catch (Exception e) {
            logger.error("EhCachedProvider.set", e);
        }
    }

    @Override
    public void remove(String key) {
        logger.debug("EhCachedProvider.remove:" + key);
        try {
            cache.remove(key);
        } catch (Exception e) {
            logger.error("EhCachedProvider.remove", e);
        }
    }

    @Override
    public synchronized void replace(String key, Object value, int expiration) {
        logger.debug(String.format("EhCachedProvider.replace: key = %s value = %s expr = %d", key, value.toString(), expiration));
        try {
            Element element = new Element(key, value);
            element.setTimeToLive(0);
            element.setTimeToIdle(expiration);
            cache.replace(element);
        } catch (Exception e) {
            logger.error("EhCachedProvider.replace", e);
        }
    }

    @Override
    public void flush() {
        logger.debug("EhCachedProvider.flushAll");
        try {
            cache.removeAll();
        } catch (Exception e) {
            logger.error("EhCachedProvider.flushAll", e);
        }
    }

    @Override
    public String getCounter(String key) {
        Object o = super.get(key);
        String counter = "0";
        if (StringUtils.isEmpty(o)) {
            set(key, counter, DEF_EXPIRATION);
        } else {
            counter = o.toString();
        }
        return counter;
    }
	
	@Override
	public void flush(String key) { 
		flush(ReadThroughCache.Type.WAP.toString(),key);
	} 
	
	@Override
	public void flush(String type,String key) { 
		String actionKey = type + "_" + key;
		int counter = Integer.valueOf(getCounter(actionKey));
		set(actionKey, counter + 1, DEF_EXPIRATION);
	} 
}
