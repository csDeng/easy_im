package com.example.easy_im.controller;

import com.example.easy_im.Context;
import com.example.easy_im.annotation.NeedToken;
import com.example.easy_im.dao.UserDao;
import com.example.easy_im.entity.User;
import com.example.easy_im.util.TokenUtil;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Example;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserDao userDao;

    @PostMapping("/login")
    public String login(@Validated @RequestBody User user) {
        User obj = userDao.findUserByNameAndPwd(user.getUserName(), user.getUserPwd());

        if(obj == null) {
            return "fail";
        }
        return TokenUtil.generateToken(obj);
    }

    @DeleteMapping("/logout")
    @NeedToken
    public String logout() {
        String token = Context.getToken();
        TokenUtil.destroyToken(token);
        return "ok";
    }

    @GetMapping("/")
    @NeedToken
    public String getToken() {
        String token = Context.getToken();
        System.out.println(token);
        return token;
    }



}
