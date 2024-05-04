package org.golive.web.starter.limit;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

    /**
     * 允许请求的量
     * @return
     */
    int limit();  // 定义限制的流量是多少

    /**
     * 限流时常
     * @return
     */
    int second();  // 定义限流的时间范围,基本单位为秒

    /**
     * 限流之后的提示内容
     * @return
     */
    String msg() default "请求过于频繁";  // 限流提示
}
