package com.paddi.netty;

import com.google.gson.Gson;
import com.paddi.common.FrameType;
import com.paddi.common.MessageReadEnum;
import com.paddi.message.Frame;
import com.paddi.message.PrivateChatMessage;
import com.paddi.service.ChatService;
import com.paddi.service.impl.ChatServiceImpl;
import com.paddi.utils.SpringBeanUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 15:16:03
 */
@Slf4j
@ChannelHandler.Sharable
public class ChatMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //握手成功事件
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ChatMessageHandler.log.info("ChatMessageHandler.userEventTriggered：协议握手完成");
            AttributeKey<Object> attributeKey = AttributeKey.valueOf("userId");
            Long userId = Long.valueOf(ctx.attr(attributeKey).get().toString());
            ChatMessageHandler.log.info("ChatMessageHandler.userEventTriggered：获取到创建连接用户ID: {}",userId);
            //可以添加Token认证
            UserChannelManager.put(userId, ctx.channel());
            if(UserChannelManager.getChannel(userId) != null) {
                ChatMessageHandler.log.info("ChatMessageHandler.userEventTriggered：userId为{}的用户连接成功",userId);
            } else {
                ctx.close();
            }
            //连接建立成功查询并返回未读消息
            ChatService chatService = (ChatService) SpringBeanUtil.getBean(ChatServiceImpl.class);
            chatService.sendUnreadMessage(ctx.channel(), userId);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        Frame frame = new Gson().fromJson(msg.text(), Frame.class);
        ChatMessageHandler.log.info("接收到消息帧: {}", frame);
        ctx.writeAndFlush(
                new TextWebSocketFrame(
                        "[服务器]在" + LocalDateTime.now()
                                + "接收到消息: " + msg.text()));
        LocalDateTime sendTime = LocalDateTime.now();
        String sequenceId = frame.getSequenceId();
        String content = frame.getContent();
        Long senderId = frame.getSenderId();
        Long receiverId = frame.getReceiverId();
        Channel senderChannel = ctx.channel();
        if(FrameType.PRIVATE_CHAT.type.equals(frame.getType())) {
            ChatMessageHandler.log.info("处理私聊消息->{}",frame);
            //私聊消息
            //判断对方是否处于在线状态
            Channel receiverChannel = UserChannelManager.getChannel(receiverId);
            if(receiverChannel == null) {
                ChatMessageHandler.log.info("用户处于离线状态");
                //用户处于离线状态->>>应该使用个推\JPush\小米推送等进行消息推送
            } else {
                ChatMessageHandler.log.info("用户处于在线状态");
                //将消息进行持久化
                //标记消息为未读取
                PrivateChatMessage message = PrivateChatMessage.builder()
                                                               .senderId(senderId)
                                                               .receiverId(receiverId)
                                                               .content(content)
                                                               .sendTime(sendTime)
                                                               .alreadyRead(MessageReadEnum.UNREAD.getStatus()).build();
                message.setId(sequenceId);
                ChatService chatService = (ChatService) SpringBeanUtil.getBean(ChatServiceImpl.class);
                try {
                    chatService.sendPrivateMessage(message);
                    ChatMessageHandler.log.info("消息序列号为:{}发送成功,消息内容为{}", sequenceId, message);
                    Frame senderResponseFrame = Frame.builder()
                                                     .sequenceId(sequenceId)
                                                     .type(FrameType.PRIVATE_CHAT_SUCCESS_RESPONSE.getType())
                                                     .extend(FrameType.PRIVATE_CHAT_SUCCESS_RESPONSE.getDescription())
                                                     .senderId(senderId)
                                                     .receiverId(receiverId).build();
                    senderChannel.writeAndFlush(
                            new TextWebSocketFrame(new Gson().toJson(senderResponseFrame))
                    );
                    receiverChannel.writeAndFlush(
                            new TextWebSocketFrame(new Gson().toJson(frame)));

                } catch(Exception e) {
                    ChatMessageHandler.log.error("消息发送失败,原因: {}",e);
                    //消息发送失败
                    Frame senderResponseFrame = Frame.builder()
                                                     .sequenceId(sequenceId)
                                                     .type(FrameType.PRIVATE_CHAT_FAIL_RESPONSE.getType())
                                                     .senderId(senderId)
                                                     .extend(FrameType.PRIVATE_CHAT_FAIL_RESPONSE.getDescription())
                                                     .receiverId(receiverId).build();
                    senderChannel.writeAndFlush(
                            new TextWebSocketFrame(new Gson().toJson(senderResponseFrame))
                    );
                }
            }
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
