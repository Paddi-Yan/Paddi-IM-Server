package websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 13:45:27
 */
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpServerCodec());
        //对写大数据的支持
        ch.pipeline().addLast(new ChunkedWriteHandler());
        //HttpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        ch.pipeline().addLast(new HttpObjectAggregator(1024*64));
        // ====================== 以上是用于支持HTTP协议    ======================

        // ====================== 以下是支持Websocket ======================
        /**
         * websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
         * 本handler会帮你处理一些繁重的复杂的事
         * 会帮你处理握手动作： handshaking（close, ping, pong） ping + pong = 心跳
         * 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
         */
        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
        ch.pipeline().addLast(new ChatHandler());
    }
}
