package com.example.easy_im.dao;

import com.example.easy_im.entity.User;
import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    @Query(value = "select id, user_name, user_pwd from im_user where user_name = ?1 and user_pwd = ?2", nativeQuery = true)
    User findUserByNameAndPwd(String name, String pwd);
}