package com.paddi.netty;

import com.google.gson.Gson;
import com.paddi.common.FrameType;
import com.paddi.message.Frame;
import io.netty.channel.Channel;
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
        Long senderId = frame.getSenderId();
        Channel channel = ctx.channel();
        if(FrameType.CONNECT.type.equals(frame.getType())) {
            //连接请求
            boolean result = UserChannelManager.put(senderId, channel);
            if(result == true) {
                ChatMessageHandler.log.info("ID为{}的用户连接成功->{}", senderId, channel);
            }else {
                ChatMessageHandler.log.warn("ID为{}的用户连接失败->{}", senderId);
            }
        } else if(FrameType.PRIVATE_CHAT.type.equals(frame.getType())) {
            //私聊消息
        } else if(FrameType.GROUP_CHAT.type.equals(frame.getType())) {
            //群聊消息
        } else if(FrameType.KEEPALIVE.type.equals(frame.getType())) {
            //心跳包
        } else if(FrameType.CLOSE.type.equals(frame.getType())) {
            //关闭连接
            UserChannelManager.remove(senderId);
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
