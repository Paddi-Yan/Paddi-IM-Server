package com.paddi.netty;

import com.google.gson.Gson;
import com.paddi.message.AbstractMessage;
import com.paddi.message.Frame;
import com.paddi.message.PrivateChatMessage;
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
 * @CreatedTime: 2022年11月24日 15:16:03
 */
@Slf4j
public class ChatMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        ChatMessageHandler.log.info("接收到消息: {}", msg.text());
        Frame frame = new Gson().fromJson(msg.text(), Frame.class);
        ChatMessageHandler.log.info("接收到消息帧: {}", frame);
        ctx.writeAndFlush(
                new TextWebSocketFrame(
                        "[服务器]在" + LocalDateTime.now()
                                + "接收到消息: " + msg.text()));
        LocalDateTime sendTime = LocalDateTime.now();
        String content = frame.getContent();
        String senderId = frame.getSenderId();
        if(AbstractMessage.PRIVATE_MESSAGE == frame.getType().intValue()) {
            //私聊
            PrivateChatMessage message = new PrivateChatMessage();

        } else if(AbstractMessage.GROUP_MESSAGE == frame.getType().intValue()) {
            //群聊
        }else  {

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChatMessageHandler.log.error("连接异常断开,原因:{}", cause.getCause().getMessage());
        ChatMessageHandler.clients.remove(ctx.channel());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ChatMessageHandler.clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ChatMessageHandler.clients.remove(ctx.channel());
    }
}
