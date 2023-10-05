package com.example.easy_im.util;

import com.example.easy_im.entity.User;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 简单模拟令牌
 */
public class TokenUtil {

    private static final Set<String> ts = new HashSet<>();

    private static final Gson gson = Singleton.gson;

    public static String generateToken(User user) {
        String json = gson.toJson(user);
        ts.add(json);
        return json;
    }

    public static boolean checkToken(String token) {
        return ts.contains(token);
    }

    public static User parseToken(String token) {
        return gson.fromJson(token, User.class);
    }

    public static void destroyToken(String token) {
        ts.remove(token);
    }

}
