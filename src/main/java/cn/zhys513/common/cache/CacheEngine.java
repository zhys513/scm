/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache;


public interface CacheEngine {
 
    /**
     * 停止缓存
     */
    public void stop();
    /**
     * 添加对象
     * @param key
     * @param value
     */
    public void add(String key, Object value);
    
    /**
     * 添加cacheName缓存中
     * @param fqn 
     * @param key
     * @param value
     */
    public void add(String cacheName, String key, Object value);
    
    /**
     * 获取换cacheName内的key
     * @param fqn
     * @param key
     * @return
     */
    public Object get(String cacheName, String key);
    
    /**
    *获取默认缓存中得
     * @param fqn
     * @return
     */
    public Object get(String key);
    

    
    /**
     * cacheName缓存中得
     * @param fqn
     * @param key
     */
    public void remove(String cacheName, String key);
    
    /**
     * 删除缓存 
     * @param fqn
     */
    public void remove(String cacheName);

}
