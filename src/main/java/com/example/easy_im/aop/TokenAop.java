package com.example.easy_im.aop;

import com.example.easy_im.Context;
import com.example.easy_im.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@Component
@Slf4j
public class TokenAop {


    @Around("@annotation(com.example.easy_im.annotation.NeedToken)")
    public Object check(ProceedingJoinPoint pjp) throws Throwable {

        try {
            HttpServletRequest servletRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpServletResponse servletResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            String authorizationHeader = servletRequest.getHeader("Authorization");
            if(StringUtils.isBlank(authorizationHeader) || servletResponse == null) {
                servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return "请携带Authorization请求头";
            }
            boolean b = TokenUtil.checkToken(authorizationHeader);
            if(!b) {
                servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return "令牌已失效";
            }

            Context.setContext(authorizationHeader);

            // 执行目标方法
            Object proceed = pjp.proceed();

            Context.clear();

            return proceed;
        } catch (Exception e) {
            return "fail";
        }

    }
}
