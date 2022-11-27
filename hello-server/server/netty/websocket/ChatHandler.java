package websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 13:50:10
 */
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 用于记录和管理所有客户端的Channel
     */
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        ChatHandler.log.info("接收到的消息: {}", content);
        /**
         * for (Channel channel: clients) {
         *     channel.writeAndFlush(
         *         new TextWebSocketFrame(
         *                 "[服务器在]" + LocalDateTime.now()
         *                 + "接受到消息, 消息为：" + content));
         * }
         * 下面这个方法，和上面的for循环，一致
         */
        ChatHandler.clients.writeAndFlush(
                new TextWebSocketFrame(
                        "[服务器]在" + LocalDateTime.now()
                                + "接收到消息: " + content));

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ChatHandler.clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当触发handlerRemoved ChannelGroup会自动移除对应的channel
        //以下语句是不必要的
        //clients.remove(ctx.channel());
        ChatHandler.log.info("客户端断开连接,Channel对应的长ID为{},Channel对应的短ID为{}", ctx.channel()
                                                                                              .id()
                                                                                              .asLongText(), ctx.channel()
                                                                                                                .id()
                                                                                                                .asShortText());
    }
}
