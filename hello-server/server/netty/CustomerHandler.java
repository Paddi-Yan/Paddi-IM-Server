import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 13:30:28
 */
@Slf4j
public class CustomerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        Channel channel = ctx.channel();
        if(msg instanceof HttpRequest) {
            CustomerHandler.log.info("Client Address: {}", channel.remoteAddress());
            ByteBuf buf = ctx.alloc().buffer().writeBytes("Hello,Netty!".getBytes());
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    buf
            );
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
            ctx.writeAndFlush(response);
        }
    }
}
