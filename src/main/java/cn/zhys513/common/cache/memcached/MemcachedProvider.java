/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache.memcached;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import cn.zhys513.common.cache.CacheConfig;
import cn.zhys513.common.cache.CacheProvider;
import cn.zhys513.common.cache.annotation.ReadThroughCache;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;

/**
 * MemcachedProvider
 * 
 * @author zhys 
 * 
 */

@Component
public class MemcachedProvider extends CacheConfig implements CacheProvider {

    private static final Logger logger = Logger.getLogger(MemcachedProvider.class);

	private static final int  DEF_EXPIRATION = 30 * 24 * 60 * 60;  // 30天
	
    @Resource(name = "memcachedClient")
    private MemcachedClient cache;
 
    @Override
    public Object get(String key) {
        logger.debug("MemcachedProvider.get:" + key);
        try {
            return cache.get(key);
        } catch (Exception e) { 
            logger.error("MemcachedProvider.get", e);
        }
        return null;
    }

    @Override
    public void set(String key, Object value, int expiration) {
        logger.debug(String.format("MemcachedProvider.set: key = %s value = %s expr = %d", key, value, expiration));
        try {
            cache.set(key, expiration, value);
        } catch (Exception e) {
            logger.error("MemcachedProvider.set", e);
        }
    }

    @Override
    public void remove(String key) {
        logger.debug("MemcachedProvider.remove:" + key);
        try {
            cache.delete(key);
        } catch (Exception e) {
            logger.error("MemcachedProvider.remove", e);
        }
    }

    @Override
    public void replace(String key, Object value, int expiration) {
        logger.debug(String.format("MemcachedProvider.replace: key = %s value = %s expr = %d", key, value.toString(), expiration));
        try {
            cache.replace(key, expiration, value);
        } catch (Exception e) {
            logger.error("MemcachedProvider.replace", e);
        }
    }
 
    @Override
    public void flush() {
        logger.debug("MemcachedProvider.flushAll");
        try {
            cache.flush();
        } catch (Exception e) {
            logger.error("MemcachedProvider.flushAll", e);
        }
    }

    /**
     * <p>
     * 扩展计数器
     * <p>
     * </br>
     * 
     * @author zhys
     * @param key
     *            交互使用 增加KEY incr •通过cache使用计数器 •使用storeCounter方法初始化一个计数器
     *            •使用incr方法对计数器增量操作 •使用decr对计数器减量操作
     */
    @Override
    public String getCounter(String key) { 
        logger.debug("MemcachedProvider.getCounter");
        CASValue<?> casv = cache.gets(key);
        if (StringUtils.isEmpty(casv)) {
            logger.debug("MemcachedProvider.getCounter...Notfound cas");
            cache.add(key, DEF_EXPIRATION, key);
        } 
        casv = cache.gets(key);
        return String.valueOf(casv.getCas());
    } 

	@Override
	public void flush(String key) { 
		flush(ReadThroughCache.Type.WAP.toString(),key);
	} 
	
	@Override
	public void flush(String type,String key) { 
		String actionKey = type + "_" + key;
		cache.incr(actionKey, 1);
	} 
	
}
