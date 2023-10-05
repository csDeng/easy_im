package com.example.easy_im.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "im_relation")
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId1;

    private int userId2;
}
