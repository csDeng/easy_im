package com.example.easy_im.handler;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    private ChatHandler chatHandler;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("{} channelRegistered", ctx.channel());
        super.channelRegistered(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常, e="+cause);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("{} channelUnregistered", ctx.channel());
        SessionManager.removeSession(ctx);
        super.channelUnregistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) {
        chatHandler.execute(ctx, textWebSocketFrame);
    }
}
