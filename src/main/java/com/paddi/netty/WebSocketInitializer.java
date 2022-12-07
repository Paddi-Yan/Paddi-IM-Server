package com.paddi.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 14:46:28
 */
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

    private WebSocketParamHandler webSocketParamHandler = new WebSocketParamHandler();

    private ChatMessageHandler chatMessageHandler = new ChatMessageHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        //WebSocket基于HTTP协议进行连接
        pipeline.addLast(new HttpServerCodec());
        //对写大数据提供支持
        pipeline.addLast(new ChunkedWriteHandler());
        // 对HttpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        // 几乎在Netty中的编程，都会使用到此Handler
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        // ====================== 以上是用于支持http协议    ======================


        // =========================== 心跳机制 ===========================
        pipeline.addLast(new IdleStateHandler(0, 0, 60));
        pipeline.addLast(new HeartBeatHandler());

        // =========================== WebSocket ===========================
        pipeline.addLast(webSocketParamHandler);
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(chatMessageHandler);

    }
}
