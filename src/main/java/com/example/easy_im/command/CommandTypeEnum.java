package com.example.easy_im.command;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandTypeEnum {
    /**
     * 系统信息
     */
    @SerializedName("10000")
    SYSTEM(10000),


    /**
     * 建立连接
     */
    @SerializedName("10001")
    CONNECTION(10001),

    /**
     * 认证
     */
    @SerializedName("10002")
    AUTH(10002),


    /**
     * 聊天
     */
    @SerializedName("10003")
    PRIVATE_CHAT(10003),

    @SerializedName("-1")
    ERROR(-1)

    ;


    private final Integer code;

}
