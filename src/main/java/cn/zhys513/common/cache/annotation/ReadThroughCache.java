/** 
 * Copyright (c) 2013,　【天黑工作室 www.zhys513.cn】  All rights reserved。
 * 
 */
package cn.zhys513.common.cache.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
 
/** 
 * <p>通过注解，最后在AOP上做缓存<p/></br>
 * <p>注意：不要使用在 增、删、改上<p/></br>
 * <p>默认设置：1分钟 <p/>
 * @author zhys(13960826213@139.com)
 * @created 2012-4-13
 */
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME) 
public @interface ReadThroughCache {
    
    /**
     * <p>定义"平台共享标示"参数<p/>
     */
    enum Type {   
        WEB,WAP,PORTAL,APP,CMS,WX,ALL
    }
    
    /**
     * <p>定义"平台共享标示"参数<p/></br> 
     */
    public abstract Type shareType() default Type.ALL;
    
    /**
     * <p> 命名空间，用来存放动作KEY的版本<p>
     * <p> ${2.xxx}第2个参数的xxx属性值作为命名空间 <p></br>
     * @author zhys
     * @return
     */ 
    public abstract String nameSpace() default "COMMON";
    
    /**
     * <p>设置缓存时间 eg.2小时: 60*60*2 <p/></br>
     * <p>默认1分钟 <p/>
     * @return
     */ 
    public abstract int expiration() default 60;
    

    /**
     * <p> 日志前缀<p></br>
     * @author zhys
     * @return
     */ 
    public abstract String prefixName() default "";
    

    /**
     * <p> 定义KEY<p>
     * <p> ${2.xxx}第2个参数的属性值作为KEY<p>
     * <p> 该参数与自动生成KEY互斥，默认为空则KEY自动生成<p>
     * </br>
     * @author zhys
     * @return
     */ 
    public abstract String key() default "";
}
