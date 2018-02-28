/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache.aop;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import cn.zhys513.common.cache.CacheKeyProvider;
import cn.zhys513.common.cache.CacheProvider;
import cn.zhys513.common.cache.annotation.ClearCacheThroughNSpace;

/**
 * @author zhys(13960826213@139.com)
 * @created 2017-5-17
 */
public class CacheClearAspect {

    private static final Logger log = Logger.getLogger(CacheClearAspect.class);
	/**
	 * 注解使用的缓存提供者 暂时提供以下两种 ehCachedProvider/memcachedProvider/redisProvider
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
	 * @param clearCacheThroughNSpace
	 * @return
	 * @throws Throwable 
	 */
	public Object around(ProceedingJoinPoint jp, ClearCacheThroughNSpace clearCacheThroughNSpace)throws Throwable{
		Object obj = jp.proceed();
 
		if (!cache.isDisable()) {
			String nameSpace = cacheKey.replaceContentByPara(clearCacheThroughNSpace.nameSpace(), jp.getArgs());
			cache.flush(clearCacheThroughNSpace.shareType().toString(), nameSpace);
			String targetName = jp.getTarget().getClass().getName();
			String methodName = jp.getSignature().getName();
			log.info("清除缓存(" + nameSpace + ")" + targetName + methodName);
		}
		return obj;
	}
}
