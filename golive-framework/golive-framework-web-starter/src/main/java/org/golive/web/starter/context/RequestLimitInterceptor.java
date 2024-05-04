package org.golive.web.starter.context;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.golive.web.starter.error.GoliveErrorException;
import org.golive.web.starter.limit.RequestLimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RequestLimitInterceptor implements HandlerInterceptor {

    @Value("spring.application.name")
    private String applicationName;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;  //HandlerMethod是SpringMVC中读入的对象,其中可以获取非常多元素
        boolean hasLimit = handlerMethod.getMethod().isAnnotationPresent(RequestLimit.class);
        // 如果当前方法有限流注解的话,就对其做一个限流判断
        if(hasLimit){
            RequestLimit requestLimit = handlerMethod.getMethod().getAnnotation(RequestLimit.class);
            Long userId = GoliveRequestContext.getUserId();  // 获取userId
            if(userId == null){
                return true;
            }
            // (userId + url + requestValue) base64 -> string (key)
            String cacheKey = applicationName + ":" + userId + ":" + request.getRequestURI();
            int limit = requestLimit.limit();
            int second = requestLimit.second();
            Integer reqTime = (Integer) Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey)).orElse(0);
            if(reqTime == 0){
                redisTemplate.opsForValue().set(cacheKey, 1, second, TimeUnit.SECONDS);
                return true;
            }else if(reqTime < limit){
                redisTemplate.opsForValue().increment(cacheKey, 1);
                return true;
            }
            throw new GoliveErrorException(-10001, requestLimit.msg());
        }
        return true;
    }
}
