/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache.aop;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StringUtils;

import cn.zhys513.common.cache.CacheKeyProvider;
import cn.zhys513.common.cache.CacheProvider;
import cn.zhys513.common.cache.annotation.ReadThroughCache;

/**
 * <p>
 * 该AOP只拦截service层且注解了memcached的方法
 * 注意：刚启动容器的时候，第一次是设置命名空间、第二次设置数据缓存，从第三次才开始获取到缓存（在没有任何缓存的情况下）
 * <p/>
 * 
 * @author zhys(13960826213@139.com)
 * @created 2012-4-14
 */  
public class CacheHandleAspect {
    private static final Logger log = Logger.getLogger(CacheHandleAspect.class);
    
    /**
     * 注解使用的缓存提供者 暂时提供以下两种
     * ehCachedProvider/memcachedProvider/redisProvider
     */
    @Resource(name = "cachedProvider")
    CacheProvider cache;
     
    /**
     * 生成缓存KEY的规则可以有不同的提供者
     */
    @Resource(name = "cacheKeyProvider")
    CacheKeyProvider cacheKey;
  

    /**
     * <p>
     * 环绕通知（Around Advice）
     * <p/>
     * 
     * @author zhys
     * @param jp
     * @param readThroughCache
     * @return
     * @throws Throwable 
     */ 
    public Object around(ProceedingJoinPoint jp, ReadThroughCache readThroughCache)throws Throwable{
        Object obj = null;
        if (cache.isDisable()) {  
			return  jp.proceed();
        }
        obj = setCache(jp, readThroughCache, obj);
        return obj;
    }

    /**
     * <p>设置缓存、缓存动作KEY、缓存数据KEY<p/>
     * @author zhys
     * @param jp
     * @param readThroughCache
     * @param obj
     * @return
     * @throws Throwable 
     */ 
    private Object setCache(ProceedingJoinPoint jp, ReadThroughCache readThroughCache, Object obj)throws Throwable{
    	String nameSpace = cacheKey.replaceContentByPara(readThroughCache.nameSpace(),jp.getArgs());
        nameSpace = readThroughCache.shareType() + "_" + nameSpace;
        String counter = cache.getCounter(nameSpace);
        if (!StringUtils.isEmpty(counter)) {
            obj = setDataCache(jp, readThroughCache, nameSpace + "_" + counter);
        } else {  
			obj = jp.proceed();
        }
        return obj;
    }

    /**
     * <p>
     * 设置数据缓存
     * <p>
     * 
     * @author zhys
     * @param jp
     * @param readThroughCache
     *            注解
     * @param version
     *            版本
     * @return 
     * @throws Throwable 
     */
    private Object setDataCache(ProceedingJoinPoint jp, ReadThroughCache readThroughCache, String version) throws Throwable{
        String key = version + "_" + cacheKey.getCacheKey(jp,readThroughCache);
        Object obj = cache.get(key); 
        String prefixName = StringUtils.isEmpty(readThroughCache.prefixName()) ? "" : "[" + readThroughCache.prefixName() + "]";
        if (obj == null || "".equals(obj)) {
            log.info(prefixName + "未取到缓存。。。。。key." + key);
			obj = jp.proceed();
		 
            if (!StringUtils.isEmpty(obj))
                cache.set(key, obj, readThroughCache.expiration());
            return obj;
        }
        log.info(prefixName + "取到缓存。。。。。key." + key);
        return obj;
    }
}
