/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache;

/**
 * CacheProvider接口，用于封装不同的缓存如：memcached或者redis等
 * 
 * @author wangchuanxi
 *
 */
	/**  
	* @author zhys513
	* @date 2013-10-14 下午3:17:21 
	*/
public interface CacheProvider {
	
	/**
     * 从缓存中获取数据
     * 
     * @param key 
     * @return 跟key关联的值，如果不存在则返回null
     */
    public Object get(String key);

    /**
     * 将value存入到缓存中
     *
     * @param key 
     * @param value
     * @param expiration 超时失效时间（秒）
     */
    public void set(String key, Object value, int expiration);
    
    /**
     * 从缓存中移除key及相关的值
     *
     * @param 
     */
    public void remove(String key);

    /**
     * 用新的值替代缓存中相同key的值
     *
     * @param key 
     * @param value 需要缓存的新值
     * @param expiration 超时失效时间（秒）
     */
    public void replace(String key, Object value, int expiration);
      
    /**
     * 递增
     * @param key
     * @param def 如果是第一次递增操作的缺省值
     * @param expiration  超时失效时间（秒）
     * @return 递增后的 值
     * public long increase(String key, long def, int expiration);
     */

    /**
     * 清空缓存
     * 
     */
    public void flush();

    /**
     * 清空命名空间缓存
     * 默认平台
     */
    public void flush(String nameSpace);
    
    /**
     * 清空命名空间缓存
     * @param type
     * 平台
     * @param nameSpace
     * 命名空间
     */
    public void flush(String type ,String nameSpace);

    /**
     * 获取计数 或 命名空间
     * 作为扩展用户统计范围KEY和销毁范围KEY使用
     * 
     * 该计数 会在其他的平台来做修改，达到其他系统修改，能够控制该计数容器里面的所有的KEY都失效
     */
    public String getCounter(String key);
 
    /**
     * 设置缓存开关状态
     * @param 
     * @return
     */
    public void setDisable(boolean disable);
    
    /**
     * 缓存开关状态
     * @return
     * @Adder by zhys513 2013-10-14 下午3:17:29  
     */
    public boolean isDisable();
}
