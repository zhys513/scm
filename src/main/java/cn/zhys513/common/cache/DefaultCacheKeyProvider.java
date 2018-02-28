/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StringUtils;

import cn.zhys513.common.cache.annotation.ReadThroughCache;
import cn.zhys513.common.cache.util.MD5Utils;

import com.alibaba.fastjson.JSON;

/**
 * CacheKeyProvider
 * 
 * @Component
 * 
 * @editor zhys
 * 
 */

public class DefaultCacheKeyProvider implements CacheKeyProvider {
	
	private static final Logger logger = Logger.getLogger(DefaultCacheKeyProvider.class);

	@Override
	public String getCacheKey(ProceedingJoinPoint jp, ReadThroughCache readThroughCache) {
		String key = readThroughCache.key();
		if(!StringUtils.isEmpty(key)){
			return replaceContentByPara(key,jp.getArgs());
		}
		String targetName = jp.getTarget().getClass().getName();
		String methodName = jp.getSignature().getName();
		Object[] arguments = jp.getArgs();
		StringBuilder sb = new StringBuilder();
		sb.append(targetName).append(".").append(methodName);
		if (arguments != null) {
			String argument = MD5Utils.getMd5Str(JSON.toJSON(arguments).toString());
			sb.append("_" + argument);
		}
		//TODO 判断长度大于多少的时候需要加密固定长度
		return sb.toString();
	}

	@Override
	public String replaceContentByPara(String content,Object[] paras) { 
		String regexA = "([$][{]\\d+[}])|([$][{]\\d+[.][a-zA-Z]\\w*[}])";
		String regexB = "[$][{]\\d+[.][a-zA-Z]\\w*[}]";
		Pattern patternA = Pattern.compile(regexA);
		Matcher matcherA = patternA.matcher(content);
		Pattern patternB = Pattern.compile(regexB);
		Matcher matcherB = patternB.matcher(content);
		int i = 0;
		String temp = "";
		List<String> lst = new ArrayList<String>();
		while (matcherA.find()) {
			Object[] obj = new Object[3];
			String group = matcherA.group();
			int starIndex = content.indexOf(group, i);
			int endIndex = starIndex + group.length();
			obj[0] = starIndex;
			obj[1] = endIndex;
			i = endIndex;
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(group);
			m.find();
			int paramterIndex = Integer.parseInt(m.group());
			Pattern p1 = Pattern.compile("[a-zA-Z]\\w*");
			Matcher m1 = p1.matcher(group);
			m1.find();
			String replaceStr = "";
			if (matcherB.find()) {
				String fieldName = m1.group();
				Object invokeObj = paras[paramterIndex];
				Class<?> a = invokeObj.getClass();
				Field field;
				try {
					field = a.getDeclaredField(fieldName);
					field.setAccessible(true);
					replaceStr = field.get(invokeObj).toString();
				} catch (NoSuchFieldException e) { 
		            logger.error("replaceContentByPara.NoSuchFieldException.", e);
				} catch (SecurityException e) { 
		            logger.error("replaceContentByPara.SecurityException.", e);
				} catch (IllegalArgumentException e) {
		            logger.error("replaceContentByPara.IllegalArgumentException.", e);
				} catch (IllegalAccessException e) {
		            logger.error("replaceContentByPara.IllegalAccessException.", e);
				}
			} else {
				replaceStr = paras[paramterIndex].toString();
			}
			lst.add(replaceStr);
		}
		String[] strList = content.split(regexA);
		for (int j = 0; j < strList.length; j++) {
			if (j != lst.size()) {
				temp += strList[j] + lst.get(j);
			} else {
				temp += strList[j];
			}
		}
		return temp;
	}

}
