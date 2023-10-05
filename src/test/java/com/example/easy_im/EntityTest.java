package com.example.easy_im;

import com.example.easy_im.dao.UserDao;
import com.example.easy_im.data.InitData;
import com.example.easy_im.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class EntityTest {


    @Resource
    private UserDao userDao;

    @Test
    public void getAllUser() {
        List<User> all = userDao.findAll();
        System.out.println(all);
    }

}
