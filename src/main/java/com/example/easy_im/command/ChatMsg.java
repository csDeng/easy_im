package com.example.easy_im.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatMsg<T> implements Serializable {

    // 消息类型
    private CommandTypeEnum type;

    // 目标接受对象
    private Integer target;

    private T content;
}
