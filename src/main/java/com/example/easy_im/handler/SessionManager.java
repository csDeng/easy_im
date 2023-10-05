package com.example.easy_im.handler;

import com.example.easy_im.command.ChatMsg;
import com.example.easy_im.entity.Msg;
import com.example.easy_im.util.Singleton;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理
 * 用于提供对session链接、断开连接、推送消息的简单控制。
 */
@Slf4j
public class SessionManager {
    /**
     * 记录当前在线的 ChannelHandlerContext
     */
    private static final Map<Integer, ChannelHandlerContext> ONLINE_SESSION = new ConcurrentHashMap<>();

    private static final Map<ChannelId, Integer> USER = new ConcurrentHashMap<>();

    public static void addSession(Integer userId, ChannelHandlerContext ctx) {
        // 此处只允许一个用户的session链接。一个用户的多个连接，我们视为无效。
        ONLINE_SESSION.putIfAbsent(userId, ctx);
        ChannelId id = ctx.channel().id();
        USER.putIfAbsent(id, userId);
        log.info("userId:{} 添加会话成功, channelId:{}", userId, id);
    }

    public static ChannelHandlerContext getSession(Integer userId) {
        return ONLINE_SESSION.getOrDefault(userId, null);
    }

    public static Integer getUserId(ChannelHandlerContext ctx) {
        ChannelId id = ctx.channel().id();
        return USER.getOrDefault(id, -1);
    }

    /**
     * 关闭session
     */
    public static void removeSession(ChannelHandlerContext ctx) {
        ChannelId id = ctx.channel().id();
        Integer userId = USER.getOrDefault(id, -1);
        ONLINE_SESSION.remove(userId);
        USER.remove(id);
        log.info("userId:{} channelId={} 删除会话成功", userId, id);
    }

    /**
     * 给单个用户推送消息
     */
    public static void sendMessage(ChannelHandlerContext ctx, ChatMsg<?> msg) {
        if (ctx == null) {
            return;
        }
        ctx.channel().writeAndFlush(new TextWebSocketFrame(Singleton.gson.toJson(msg)));
    }
}
