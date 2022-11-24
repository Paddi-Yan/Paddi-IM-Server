import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月23日 23:05:34
 */
@Slf4j
@ChannelHandler.Sharable
public class HelloServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("loggingHandler", new LoggingHandler());
        ch.pipeline().addLast("httpServerHandler", new HttpServerCodec());
        ch.pipeline().addLast("customerHandler", new CustomerHandler());
    }
}
