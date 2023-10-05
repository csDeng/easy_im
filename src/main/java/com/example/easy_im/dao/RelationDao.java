package com.example.easy_im.dao;

import com.example.easy_im.entity.Relation;
import com.example.easy_im.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationDao extends JpaRepository<Relation, Integer> {

}