package com.example.easy_im.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {

    private String msg;

    private Integer code;


    public static Result ok(String m) {
        return new Result(m, 200);
    }

    public static Result fail(String m) {
        return new Result(m, 400);
    }

}
