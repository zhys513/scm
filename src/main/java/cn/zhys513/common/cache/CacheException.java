/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache;

/**
 * 缓存操作例外
 * 
 * @author wangchuanxi
 *
 */
public class CacheException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    /**
     * 缺省构造函数
     */
    public CacheException() {
        super();
    }

    /**
     * 使用message构造一个新的Exception
     *
     * @param message 详细的例外信息
     */
    public CacheException(String message) {
        super(message);
    }

    /**
     * 使用message和throwable构造例外
     *
     * @param message   详细的例外信息
     * @param throwable 
     */
    public CacheException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * 使用cause构造例外
     * 
     * @param cause
     */
    public CacheException(Throwable cause) {
        super(cause);
    }
}
