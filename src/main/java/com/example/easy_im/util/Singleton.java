package com.example.easy_im.util;

import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Getter
public class Singleton {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final Gson gson = new Gson();
}
