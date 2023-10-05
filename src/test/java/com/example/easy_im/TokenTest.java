package com.example.easy_im;

import com.example.easy_im.entity.User;
import com.example.easy_im.util.TokenUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TokenTest {

    @Resource
    private TokenUtil tokenUtil;

    @Test
    public void test() {
        User user = new User();
        user.setId(1);
        user.setUserName("hello");
        String s = tokenUtil.generateToken(user);
        System.out.println(s);

        User parsedToken = tokenUtil.parseToken(s);
        System.out.println(parsedToken.toString());
    }

}
