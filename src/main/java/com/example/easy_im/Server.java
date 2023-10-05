package com.example.easy_im;

import com.example.easy_im.handler.AuthHandler;
import com.example.easy_im.handler.WebsocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Server {

    private Channel channel;

    @Value("${cfg.ws-port}")
    private Integer port;

    @Resource
    private WebsocketHandler websocketHandler;


    private final NioEventLoopGroup boss = new NioEventLoopGroup();
    private final NioEventLoopGroup worker = new NioEventLoopGroup();

    @PostConstruct
    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // HttpServerCodec is a class provided by Netty which does the decoding and encoding for incoming requests.
                        pipeline.addLast(new HttpServerCodec())
                                .addLast(new ChunkedWriteHandler())
                                // http 消息聚合操作 -> FullHttpRequest FullHttpResponse
                                .addLast(new HttpObjectAggregator(1024 * 64))
                                // websocket 自动握手连接
                                .addLast(new WebSocketServerProtocolHandler("/"))

                                // 身份认证
                                .addLast(new AuthHandler())

                                // websocket handler
                                .addLast(websocketHandler);
                    }
                });
        ChannelFuture future = bootstrap.bind(port).sync();
        if (!future.isSuccess()) {
            log.error("服务启动失败");
            return;
        }
        log.info("netty server has bind :8000");
        channel = future.channel();

    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (channel != null) {
            // 关闭 Netty Server
            channel.close().sync();
        }
        //优雅关闭两个 EventLoopGroup 对象
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
