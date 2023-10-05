package com.example.easy_im.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "im_msg")
public class Msg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    private int toUserId;

    private int fromUserId;

    private int hasRead;
}
