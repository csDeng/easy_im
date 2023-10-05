package com.example.easy_im;

import com.example.easy_im.entity.User;
import com.example.easy_im.util.TokenUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Context {

    public static final ThreadLocal<String> context = new ThreadLocal<>();

    public static void setContext(String token) {
        context.set(token);
    }

    public static String getToken() {
        return context.get();
    }

    public static User getUser() {
        String s = context.get();
        return TokenUtil.parseToken(s);
    }

    public static void clear() {
        log.info("清除上下文");
        context.remove();
    }
}
