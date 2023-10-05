package com.example.easy_im.handler;

import com.example.easy_im.command.ChatMsg;
import com.example.easy_im.command.CommandTypeEnum;
import com.example.easy_im.dao.MsgDao;
import com.example.easy_im.data.Result;
import com.example.easy_im.entity.Msg;
import com.example.easy_im.util.Singleton;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ChatHandler {

    @Resource
    private MsgDao msgDao;


    private static final Gson gs = new Gson();

    public void execute(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        try {
            Gson gson = Singleton.gson;
            ChatMsg chatMsg = gson.fromJson(frame.text(), ChatMsg.class);
            switch (chatMsg.getType()) {
                case PRIVATE_CHAT -> {
                    log.info("private");
                    Integer target = chatMsg.getTarget();
                    if(target == null) {
                        sendMsg(ctx, "请指定消息接收者");
                        return;
                    }
                    Integer userId = SessionManager.getUserId(ctx);
                    if(userId == -1) {
                        sendMsg(ctx, "获取自身userId失败");
                        return;
                    }
                    log.info("给{}发送消息", target);
                    Msg msg = new Msg();
                    msg.setFromUserId(userId);
                    msg.setToUserId(target);
                    msg.setContent(chatMsg.getContent().toString());
                    ChannelHandlerContext session = SessionManager.getSession(target);
                    if(session == null) {
                        sendMsg(ctx, "userId="+ target + "不在线");
                        msg.setHasRead(0);
                        msgDao.save(msg);
                        return;
                    }
                    sendMsg(session, chatMsg.getContent().toString());
                    msg.setHasRead(1);
                    msgDao.save(msg);
                }
                default -> sendMsg(ctx,"暂不支持当前消息类型");
            }
        }catch (Exception e) {
            log.error(ChatHandler.class.getSimpleName() + "occurs error: {}", e.getMessage());
            sendMsg(ctx, "数据格式有误");
        }
    }

    private void sendMsg(ChannelHandlerContext ctx, String data) {
        ChatMsg<String> chatMsg = ChatMsg.<String>builder()
                .type(CommandTypeEnum.SYSTEM)
                .content(data)
                .build();
        String responseJson = gs.toJson(chatMsg);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseJson));
    }

}
