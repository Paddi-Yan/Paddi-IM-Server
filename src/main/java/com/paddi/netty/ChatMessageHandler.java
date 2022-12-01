package com.paddi.netty;

import com.google.gson.Gson;
import com.paddi.common.FrameType;
import com.paddi.common.MessageReadEnum;
import com.paddi.common.MessageType;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import com.paddi.message.Frame;
import com.paddi.message.PrivateChatMessage;
import com.paddi.service.ChatService;
import com.paddi.service.FriendService;
import com.paddi.service.UserService;
import com.paddi.service.impl.ChatServiceImpl;
import com.paddi.service.impl.FriendServiceImpl;
import com.paddi.service.impl.UserServiceImpl;
import com.paddi.utils.SpringBeanUtil;
import com.paddi.utils.mapstruct.UserMapStruct;
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
import java.util.*;

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
            //TODO 连接建立成功查询并返回未读消息
            ChatService chatService = (ChatService) SpringBeanUtil.getBean(ChatServiceImpl.class);
            chatService.sendUnreadMessage(ctx.channel(), userId);
            //TODO 返回当前在线的好友列表
            FriendService friendService = (FriendService) SpringBeanUtil.getBean(FriendServiceImpl.class);
            List<User> allFriendList = friendService.getFriendList(userId);
            List<UserVo> onlineFriendList = new ArrayList<>();
            List<UserVo> notOnlineFriendList = new ArrayList<>();
            Iterator<User> iterator = allFriendList.iterator();
            if(iterator.hasNext()) {
                User friend = iterator.next();
                UserVo friendVo = UserMapStruct.USER_MAPPING.userToUserVo(friend);
                //筛选不在线的好友
                if(UserChannelManager.contains(friend.getId())) {
                    //在线
                    onlineFriendList.add(friendVo);
                }else {
                    //不在线
                    notOnlineFriendList.add(friendVo);
                }
            }
            HashMap<String, List> friendList = new HashMap<>();
            friendList.put("online", onlineFriendList);
            friendList.put("notOnline", notOnlineFriendList);
            Frame frame = Frame.builder().extend(friendList).type(FrameType.FRIEND_LIST.getType()).build();
            ctx.channel().writeAndFlush(new TextWebSocketFrame(
                    new Gson().toJson(frame)
            ));
            log.info("推送好友列表成功");
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
        Long senderId = frame.getSenderId();
        Long receiverId = frame.getReceiverId();
        String content = frame.getContent();
        LocalDateTime sendTime = LocalDateTime.now();
        String sequenceId = frame.getSequenceId();
        Channel senderChannel = ctx.channel();
        Channel receiverChannel = UserChannelManager.getChannel(receiverId);
        if(FrameType.PRIVATE_CHAT.type.equals(frame.getType())) {
            //TODO 私聊消息
            ChatMessageHandler.log.info("处理私聊消息->{}",frame);
            //好友关系校验
            Boolean isFriend = checkRelationShip(frame, senderChannel);
            if(!isFriend) {
                return;
            }
            Boolean needPersistence = true;
            //判断对方是否处于在线状态
            if(receiverChannel == null) {
                //用户处于离线状态->>>应该使用个推\JPush\小米推送等进行消息推送
            } else {
                //WebSocket推送消息
                //如果消息推送失败会将needPersistence置为false
                //即需要重新进行消息的发送
                //不需要消息的持久化
                needPersistence = ChatMessageHandler.sendPrivateMessage(frame, senderChannel, receiverChannel);
            }
            //将消息进行持久化
            if(needPersistence) {
                PrivateChatMessage message = PrivateChatMessage.builder()
                                                               .senderId(senderId)
                                                               .content(content)
                                                               .sendTime(sendTime)
                                                               .receiverId(receiverId)
                                                               .type(MessageType.TEXT.getCode())
                                                               .alreadyRead(MessageReadEnum.UNREAD.getStatus()).build();
                message.setId(sequenceId);
                messagePersistence(message);
            }
        } else if(FrameType.READ_PRIVATE_MESSAGE.type.equals(frame.getType())) {
            //TODO 标记私聊消息为已读消息
            //拓展信息Map中必须包含用户ID和好友ID
            Map extend = frame.getExtend();
            if(extend == null || extend.isEmpty() || !extend.containsKey("userId")
                    || !extend.containsKey("friendId") || extend.get("userId") == null || extend.get("friendId") == null ) {
                return;
            }
            ChatService chatService = (ChatService) SpringBeanUtil.getBean(ChatServiceImpl.class);
            //将该用户与该好友的所有消息标记为已读
            chatService.signMessageAlreadyRead(extend);
        } else if(FrameType.GROUP_CHAT.type.equals(frame.getType())) {
            //TODO 群聊消息
        } else if(FrameType.PRIVATE_FILE_MESSAGE.type.equals(frame.getType())) {
            //TODO 私信文件传输
            //检查是否是好友关系
            Boolean isFriend = checkRelationShip(frame, senderChannel);
            if(!isFriend) {
                return;
            }
            //获取文件传输中的拓展信息
            Map<String, String> extend = frame.getExtend();
            //Map中需要包含两个信息 fileName/size
            int requestSize = 2;
            if(extend == null || extend.size() < requestSize || extend.get("fileName") == null || extend.get("size") == null) {
                return;
            }
            Boolean needPersistence = true;
            if(receiverChannel == null) {
                //不在线-暂时不做处理
            }else {
                //在线-WebSocket发送消息
                needPersistence = sendPrivateMessage(frame, senderChannel, receiverChannel);
            }
            if(needPersistence) {
                PrivateChatMessage message = PrivateChatMessage.builder()
                                                             .senderId(senderId)
                                                             .sendTime(LocalDateTime.now())
                                                             .receiverId(receiverId)
                                                             .alreadyRead(MessageReadEnum.UNREAD.getStatus())
                                                             .type(MessageType.FILE.getCode())
                                                             .extendName(extend.get("fileName"))
                                                             .extendSize(extend.get("size"))
                                                             .build();
                message.setId(frame.getSequenceId());
                messagePersistence(message);
            }
        } else if(FrameType.KEEPALIVE.type.equals(frame.getType())) {
            //TODO 心跳包
        } else if(FrameType.CLOSE.type.equals(frame.getType())) {
            //TODO 关闭连接
            UserChannelManager.remove(senderId);
        }
    }

    private static Boolean checkRelationShip(Frame frame, Channel senderChannel) {
        Long senderId = frame.getSenderId();
        Long receiverId = frame.getReceiverId();
        String sequenceId = frame.getSequenceId();
        String content = frame.getContent();
        UserService userService = (UserService) SpringBeanUtil.getBean(UserServiceImpl.class);
        HashMap<String, Object> map = userService.preConditionSearchUser(senderId, receiverId);
        Integer status = (Integer) map.get("status");
        //检查是否为好友关系
        if(!SearchUserStatusEnum.ALREADY_FRIENDS.status.equals(status)) {
            //非好友关系
            senderChannel.writeAndFlush(new TextWebSocketFrame(
                    new Gson().toJson(new Frame(sequenceId, senderId, content, receiverId, FrameType.AUTHORIZATION_WARNING_MESSAGE.getType(), null))
            ));
            return false;
        }
        return true;
    }

    private static Boolean sendPrivateMessage(Frame frame, Channel senderChannel, Channel receiverChannel) {
        Long senderId = frame.getSenderId();
        Long receiverId = frame.getSenderId();
        String content = frame.getContent();
        String sequenceId = frame.getSequenceId();
        try {
            Frame senderResponseFrame = Frame.builder()
                                             .sequenceId(sequenceId)
                                             .type(FrameType.PRIVATE_CHAT_SUCCESS_RESPONSE.getType())
                                             .senderId(senderId)
                                             .receiverId(receiverId)
                                             .extend(frame.getExtend()).build();
            receiverChannel.writeAndFlush(
                    new TextWebSocketFrame(new Gson().toJson(frame)));
            senderChannel.writeAndFlush(
                    new TextWebSocketFrame(new Gson().toJson(senderResponseFrame))
            );
            return true;
        } catch(Exception e) {
            ChatMessageHandler.log.error("消息发送失败,原因: {}", e);
            //消息发送失败
            Frame senderResponseFrame = Frame.builder()
                                             .sequenceId(sequenceId)
                                             .type(FrameType.PRIVATE_CHAT_FAIL_RESPONSE.getType())
                                             .senderId(senderId)
                                             .receiverId(receiverId).build();
            senderChannel.writeAndFlush(
                    new TextWebSocketFrame(new Gson().toJson(senderResponseFrame))
            );
            return false;
        }
    }

    private static void messagePersistence(PrivateChatMessage message){
        ChatService chatService = (ChatService) SpringBeanUtil.getBean(ChatServiceImpl.class);
        chatService.sendPrivateMessage(message);
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
