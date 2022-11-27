package com.paddi.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 14:38:46
 */
@Component
@Slf4j
public class WebSocketServer {

    private static class WebSocketServerFactory {
        static final WebSocketServer instance = new WebSocketServer();
    }

    public static WebSocketServer getInstance() {
        return WebSocketServerFactory.instance;
    }

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ServerBootstrap serverBootstrap;

    private ChannelFuture channelFuture;

    public WebSocketServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                                               .channel(NioServerSocketChannel.class)
                                               .childHandler(new WebSocketInitializer());
    }

    public void start() {
        this.channelFuture = serverBootstrap.bind(8888);
        WebSocketServer.log.info("Netty WebSocket Server Is Running....");
    }

    public static void main(String[] args) {
        WebSocketServer.getInstance().start();
    }
}
