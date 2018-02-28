/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache;

import org.aspectj.lang.ProceedingJoinPoint;

import cn.zhys513.common.cache.annotation.ReadThroughCache;

/**  
	* @author zhys513
	* @date 2013-8-23 下午4:22:13 
	*/
public interface CacheKeyProvider { 
    
	 /**
     * 
     * <p>
     * function:获得cache key的方法，
     * <p/>
     * <p>
     * cache key是Cache中一个Element的唯一标识 cache key包括
     * <p/>
     * <p>
     * 包名+类名
     * <p/>
     * <p>
     * 方法名 + 参数.json，如:
     * <p/>
     * <p>
     * com.zhys513.framework.wap.service.test.impl.TbTestServiceImpl.queryTbTest [{"id":1,
     * "testVarchar1" :"","testVarchar2":"","testClob"
     * :"","testBoolean":true,"testNumber" :1,"testVarchar3":""}]
     * <p/> 
     * @param targetName
     * @param methodName
     * @param arguments
     * @return
     */
    String getCacheKey(ProceedingJoinPoint jp, ReadThroughCache readThroughCache);
    
 
    /**
     * 根据变量替换参数
     * 替换方法：content中带有${1.xxx}的内容，根据jp中实际的传参进行替换
     * @param paras
     * 实际方法传入参数，下标从0开始。
     * @param content
     * 被替换的文本
     * @return
     */
    String replaceContentByPara(String content,Object[] paras);
}
