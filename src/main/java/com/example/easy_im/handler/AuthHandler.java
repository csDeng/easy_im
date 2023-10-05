package com.example.easy_im.handler;

import com.example.easy_im.command.ChatMsg;
import com.example.easy_im.command.CommandTypeEnum;
import com.example.easy_im.entity.User;
import com.example.easy_im.util.TokenUtil;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Gson gs = new Gson();

    private static final long AUTH_TIMEOUT = 30; // 3分钟，单位为秒

    private static final long REMINDER_INTERVAL = 5; // 单位为秒

    private ScheduledFuture<?> timeoutFuture;
    private ScheduledFuture<?> reminderFuture;


    @Override
    public void channelActive(ChannelHandlerContext ctx){
        startHelper(ctx);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        String text = frame.text();

        try {
            ChatMsg chatMsg = gs.fromJson(text, ChatMsg.class);
            if(chatMsg.getType() != CommandTypeEnum.AUTH) {
                sendAuthResponse(ctx, false, "请先传输认证数据包！");
                return;
            }
            String token = (String) chatMsg.getContent();
            if (StringUtils.isBlank(token)) {
                sendAuthResponse(ctx, false, "请提供有效的令牌");
                return;
            }

            boolean isValidToken = TokenUtil.checkToken(token);
            if (!isValidToken) {
                sendAuthResponse(ctx, false, "令牌失效，请重新获取令牌");
                return;
            }

            sendAuthResponse(ctx, false, "认证成功");
            User user = TokenUtil.parseToken(token);

            // 将认证处理器从pipe删除
            ctx.channel().pipeline().remove(this);

            // 注意删除后再添加 channel 会话，不然没办法反向获取 userId
            SessionManager.addSession(user.getId(), ctx);
            stopHelper();
        } catch (Exception e) {
            sendAuthResponse(ctx, false, "请确认数据格式");
            log.error("认证错误{}", e.getMessage());
        }

    }


    private void sendAuthResponse(ChannelHandlerContext ctx, boolean close, String message) {
        ChatMsg<String> chatMsg = ChatMsg.<String>builder()
                .type(CommandTypeEnum.SYSTEM)
                .content(message)
                .build();
        String responseJson = gs.toJson(chatMsg);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseJson));
        if (close) {
            log.info("认证失败");
            ctx.close();
        }
    }

    private void startAuthTimeoutTimer(ChannelHandlerContext ctx) {
        timeoutFuture = ctx.executor().schedule(() -> {
            log.info("连接关闭");
            sendAuthResponse(ctx, true, "超时未接收到认证包，连接关闭");
            cancelAuthReminderTimer();
        }, AUTH_TIMEOUT, TimeUnit.SECONDS);
    }

    private void cancelAuthTimeoutTimer() {
        if (timeoutFuture != null && !timeoutFuture.isDone()) {
            timeoutFuture.cancel(true);
        }
    }

    private void startAuthReminderTimer(ChannelHandlerContext ctx) {
        reminderFuture = ctx.executor().scheduleAtFixedRate(() -> {
            // 在每一分钟执行的操作
            // 发送催促认证消息
            log.info("催促认证");
            sendAuthResponse(ctx, false, "请尽快进行认证");
        }, REMINDER_INTERVAL, REMINDER_INTERVAL, TimeUnit.SECONDS);
    }

    private void cancelAuthReminderTimer() {
        if (reminderFuture != null && !reminderFuture.isDone()) {
            reminderFuture.cancel(true);
        }
    }

    private void startHelper(ChannelHandlerContext ctx) {
        startAuthTimeoutTimer(ctx);
        startAuthReminderTimer(ctx);
    }

    private void stopHelper() {
        cancelAuthTimeoutTimer();
        cancelAuthReminderTimer();
    }
}