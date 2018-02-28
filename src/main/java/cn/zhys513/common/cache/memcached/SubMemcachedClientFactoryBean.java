/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache.memcached;

import net.spy.memcached.spring.MemcachedClientFactoryBean;

public class SubMemcachedClientFactoryBean extends MemcachedClientFactoryBean {

	private boolean disable = false;

	public Object getObject() throws Exception {
		if(disable){
			return null;
		}
		return super.getObject();
	} 

	/**
	 * @param disable
	 * @return
	 */
	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public boolean isDisable() {
		return disable;
	}
}
