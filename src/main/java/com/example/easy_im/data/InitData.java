package com.example.easy_im.data;

import com.example.easy_im.dao.UserDao;
import com.example.easy_im.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class InitData {

    @Resource
    private UserDao userDao;

    @PostConstruct
    public void init() {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setId(i);
            user.setUserName("name"+i);
            user.setUserPwd("pwd"+i);
            users.add(user);
        }
        List<User> userList = userDao.saveAll(users);
        log.info("init users={}", userList);

    }
}
