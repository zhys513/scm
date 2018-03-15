/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import cn.zhys513.common.cache.CacheConfig;
import cn.zhys513.common.cache.CacheProvider;
import cn.zhys513.common.cache.annotation.ReadThroughCache;
 
/**
 * RedisProvider
 * 
 * @author zhys 
 * 
 */

public class RedisProvider extends CacheConfig implements CacheProvider {

    private static final Logger logger = Logger.getLogger(RedisProvider.class);

	private static final int  DEF_EXPIRATION = 30 * 24 * 60 * 60;  // 30天
	
    @Resource(name = "jedisPool") 
    private JedisPool pool;
 
    @Override
    public Object get(String key) {
        logger.debug("JedisProvider.get:" + key);
        
        Object obj = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        Jedis jedis = pool.getResource();
        try {
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes == null)
                return null;

            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) { 
            logger.error("JedisProvider.get", e);
        } finally {
            pool.returnResource(jedis);
            try {
                if (ois != null)
                    ois.close();
                if (bais != null)
                    bais.close();
            } catch (IOException e) { 
                logger.error("JedisProvider.get", e);
            }
        }

        return obj;
    }


    @Override
    public void set(String key, Object value, int expiration) {
        logger.debug(String.format("JedisProvider.set: key = %s value = %s expr = %d", key, value, expiration));
       
        if (!StringUtils.isEmpty(value)) {
            ByteArrayOutputStream baos = null;
            ObjectOutputStream oos = null;

            Jedis jedis = pool.getResource();
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(value);

                jedis.setex(key.getBytes("utf-8"), expiration, baos.toByteArray()); 
            } catch (Exception e) { ;
                logger.error("JedisProvider.set", e);
            } finally {
                pool.returnResource(jedis); 
                try {
                    if (baos != null)
                        baos.close();
                    if (oos != null)
                        oos.close();
                } catch (IOException e) {
                    logger.error("JedisProvider.set", e);
                }
            }
        }
    }

    @Override
    public void remove(String key) {
        logger.debug("JedisProvider.remove:" + key);

        Jedis jedis = pool.getResource();
        try {
            jedis.del(key);
        } catch (Exception e) { 
            logger.error("JedisProvider.remove", e);
        }finally {
            pool.returnResource(jedis); 
        }
    }

    @Override
    public void replace(String key, Object value, int expiration) {
        logger.debug(String.format("JedisProvider.replace: key = %s value = %s expr = %d", key, value.toString(), expiration));
        
        if (!StringUtils.isEmpty(value)) {
            ByteArrayOutputStream baos = null;
            ObjectOutputStream oos = null;

            Jedis jedis = pool.getResource();
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(value);

                jedis.setex(key.getBytes("utf-8"), expiration, baos.toByteArray()); 
            } catch (Exception e) { ; 
                logger.error("JedisProvider.replace", e);
            } finally {
                pool.returnResource(jedis); 
                try {
                    if (baos != null)
                        baos.close();
                    if (oos != null)
                        oos.close();
                } catch (IOException e) {
                    logger.error("JedisProvider.replace", e);
                }
            }
        }
    }
 
    @Override
    public void flush() {
        logger.debug("JedisProvider.flushAll");
        
        Jedis jedis = pool.getResource();
        try {
            jedis.flushDB();
        } catch (Exception e) {
            logger.error("JedisProvider.flushAll", e);
        }finally {
            pool.returnResource(jedis); 
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
        Jedis jedis = pool.getResource();
        String counter = "0";
        try {
            String str = jedis.get(key);
            if (StringUtils.isEmpty(str)) {
                jedis.setex(key, DEF_EXPIRATION, counter);
            } else {
                counter = str;
            }
        } finally {
            pool.returnResource(jedis);
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
        Jedis jedis = pool.getResource();
		int counter = Integer.valueOf(getCounter(actionKey)) + 1;
		jedis.setex(actionKey, DEF_EXPIRATION, String.valueOf(counter));
	} 
}
