package com.paddi.netty;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.paddi.common.FrameType;
import com.paddi.common.MessageReadEnum;
import com.paddi.common.MessageType;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.User;
import com.paddi.message.Frame;
import com.paddi.message.PrivateChatMessage;
import com.paddi.message.UserInfo;
import com.paddi.service.ChatService;
import com.paddi.service.FriendService;
import com.paddi.service.UserService;
import com.paddi.service.impl.ChatServiceImpl;
import com.paddi.service.impl.FriendServiceImpl;
import com.paddi.service.impl.UserServiceImpl;
import com.paddi.utils.SpringBeanUtil;
import com.paddi.utils.mapstruct.UserMapStruct;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 15:16:03
 */
@Slf4j
@ChannelHandler.Sharable
public class ChatMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //握手成功事件
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ChatMessageHandler.log.info("ChatMessageHandler.userEventTriggered：协议握手完成");
            AttributeKey<Object> attributeKey = AttributeKey.valueOf("userId");
            Long userId = Long.valueOf(ctx.attr(attributeKey).get().toString());
            //TODO 添加Token认证
            UserService userService = (UserService) SpringBeanUtil.getBean(UserServiceImpl.class);
            User user = userService.getBaseMapper().selectById(userId);
            if(user == null || !user.getEnabled()) {
                Frame failResponseFrame = Frame.builder().senderId(userId.toString())
                                   .type(FrameType.FORBIDDEN.getType())
                                   .extend(ImmutableMap.of("cause", "用户不存在或者未开放权限"))
                                   .build();
                ChannelFuture channelFuture = ctx.channel()
                                                 .writeAndFlush(new TextWebSocketFrame(new Gson().toJson(failResponseFrame)));
                channelFuture.addListener(promise -> {
                    handlerRemoved(ctx);
                    ctx.close();
                    log.info("用户不存在或者为开放权限,连接已断开!");
                });
                ctx.channel().closeFuture().sync();
                return;
            }
            ChatMessageHandler.log.info("ChatMessageHandler#userEventTriggered：获取到创建连接用户ID: {}",userId);
            UserChannelManager.put(userId, ctx.channel());
            //TODO 连接建立成功查询并返回未读消息
            ChatService chatService = (ChatService) SpringBeanUtil.getBean(ChatServiceImpl.class);
            Map<String, Integer> friendToCount = chatService.getUnreadMessageCount(userId);
            //TODO 返回当前在线和不在线的好友列表
            FriendService friendService = (FriendService) SpringBeanUtil.getBean(FriendServiceImpl.class);
            List<User> allFriendList = friendService.getFriendList(userId);
            HashMap<String, UserInfo> onlineFriendMap = new HashMap<>();
            HashMap<String, UserInfo> notOnlineFriendMap = new HashMap<>();
            Iterator<User> iterator = allFriendList.iterator();
            UserInfo userInfo = UserMapStruct.USER_MAPPING.userToUserInfo(user);
            Frame onLinedFriendInfo = Frame.builder()
                                           .type(FrameType.FRIEND_ONLINE.getType())
                                           .senderId(userId.toString())
                                           .extend(ImmutableMap.of("onLinedFriendInfo", userInfo))
                                           .build();
            while(iterator.hasNext()) {
                User friend = iterator.next();
                //筛选不在线的好友
                UserInfo friendInfo = UserMapStruct.USER_MAPPING.userToUserInfo(friend);
                friendInfo.setUnreadCount(friendToCount.getOrDefault(friendInfo.getId(), 0));
                if(UserChannelManager.contains(friend.getId())) {
                    //在线
                    onlineFriendMap.put(friend.getId().toString(), friendInfo);
                    //TODO 向好友推送上线消息-更新好友的在线好友列表和不在线的好友列表
                    Channel channel = UserChannelManager.getChannel(friend.getId());
                    onLinedFriendInfo.setReceiverId(friend.getId().toString());
                    channel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(onLinedFriendInfo)));
                }else {
                    //不在线
                    notOnlineFriendMap.put(friend.getId().toString(), friendInfo);
                }
            }
            HashMap<String, HashMap<String, UserInfo>> friendList = new HashMap<>(2);
            friendList.put("online", onlineFriendMap);
            friendList.put("notOnline", notOnlineFriendMap);
            Frame frame = Frame.builder().extend(friendList).type(FrameType.FRIEND_LIST.getType()).build();
            ctx.channel().writeAndFlush(new TextWebSocketFrame(
                    new Gson().toJson(frame)
            ));
            log.info("推送好友列表成功");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
//        Frame frame = JSON.parseObject(msg.text(), Frame.class);
        Frame frame = new Gson().fromJson(msg.text(), Frame.class);
        ChatMessageHandler.log.info("接收到消息帧: {}", frame);
        /*ctx.writeAndFlush(
                new TextWebSocketFrame(
                        "[服务器]在" + LocalDateTime.now()
                                + "接收到消息: " + msg.text()));*/
        Long senderId = Long.valueOf(frame.getSenderId());
        Long receiverId = Long.valueOf(frame.getReceiverId());
        String content = frame.getContent();
        LocalDateTime sendTime = LocalDateTime.now();
        String sequenceId = frame.getSequenceId();
        Channel senderChannel = ctx.channel();
        Channel receiverChannel = UserChannelManager.getChannel(receiverId);
        ChatService chatService = (ChatService) SpringBeanUtil.getBean(ChatServiceImpl.class);
        if(FrameType.PRIVATE_CHAT.getType().equals(frame.getType())) {
            //TODO 私聊消息
            ChatMessageHandler.log.info("处理私聊消息->{}",frame);
            //好友关系校验
            Boolean isFriend = checkRelationShip(frame, senderChannel);
            if(!isFriend) {
                return;
            }
            Boolean needPersistence = true;
            //TODO 判断消息是否已经发送过
            Boolean checkMessageSend = chatService.messageAlreadySend(sequenceId);
            //消息已经发送了
            if(checkMessageSend) {
                frame.setType(FrameType.DUPLICATE_SEND.getType());
                frame.setExtend(ImmutableMap.of("cause", "该消息重复发送"));
                senderChannel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(frame)));
                return;
            }
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
                //发送未读消息通知给好友
                chatService.sendUnreadMessage(receiverChannel, receiverId);
            }
        } else if(FrameType.READ_PRIVATE_MESSAGE.getType().equals(frame.getType())) {
            //TODO 该功能已弃用
            //TODO 标记私聊消息为已读消息
            //拓展信息Map中必须包含用户ID和好友ID
            Map extend = frame.getExtend();
            if(extend == null || extend.isEmpty() || !extend.containsKey("userId")
                    || !extend.containsKey("friendId") || extend.get("userId") == null || extend.get("friendId") == null ) {
                return;
            }
            //将该用户与该好友的所有消息标记为已读
            Long userId = (Long) extend.get("userId");
            Long friendId = (Long) extend.get("friendId");
            chatService.signMessageAlreadyRead(userId, friendId);
        } else if(FrameType.GROUP_CHAT.getType().equals(frame.getType())) {
            //TODO 群聊消息
        } else if(FrameType.PRIVATE_FILE_MESSAGE.getType().equals(frame.getType())) {
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
        } else if(FrameType.KEEPALIVE.getType().equals(frame.getType())) {
            //TODO 心跳包
            log.info("接收到来自用户[{}]的心跳包",frame.getSenderId());
        } else if(FrameType.CLOSE.getType().equals(frame.getType())) {
            //TODO 关闭连接
            UserChannelManager.remove(senderId);
            ctx.close();
        }
    }

    private static Boolean checkRelationShip(Frame frame, Channel senderChannel) {
        Long senderId = Long.valueOf(frame.getSenderId());
        Long receiverId = Long.valueOf(frame.getReceiverId());
        String sequenceId = frame.getSequenceId();
        String content = frame.getContent();
        UserService userService = (UserService) SpringBeanUtil.getBean(UserServiceImpl.class);
        HashMap<String, Object> map = userService.preConditionSearchUser(senderId, receiverId);
        Integer status = (Integer) map.get("status");
        //检查是否为好友关系
        if(!SearchUserStatusEnum.ALREADY_FRIENDS.status.equals(status)) {
            //非好友关系
            senderChannel.writeAndFlush(new TextWebSocketFrame(
                    new Gson().toJson(new Frame(sequenceId, senderId.toString(), content, receiverId.toString(), FrameType.AUTHORIZATION_WARNING_MESSAGE.getType(), null))
            ));
            return false;
        }
        return true;
    }

    private static Boolean sendPrivateMessage(Frame frame, Channel senderChannel, Channel receiverChannel) {
        Long senderId = Long.valueOf(frame.getSenderId());
        Long receiverId = Long.valueOf(frame.getSenderId());
        String content = frame.getContent();
        String sequenceId = frame.getSequenceId();
        try {
            Frame senderResponseFrame = Frame.builder()
                                             .sequenceId(sequenceId)
                                             .type(FrameType.PRIVATE_CHAT_SUCCESS_RESPONSE.getType())
                                             .senderId(senderId.toString())
                                             .receiverId(receiverId.toString())
                                             .extend(frame.getExtend()).build();
            receiverChannel.writeAndFlush(
                    new TextWebSocketFrame(new Gson().toJson(frame)));
            senderChannel.writeAndFlush(
                    new TextWebSocketFrame(new Gson().toJson(senderResponseFrame)));

            return true;
        } catch(Exception e) {
            ChatMessageHandler.log.error("消息发送失败,原因: {}", e);
            //消息发送失败
            Frame senderResponseFrame = Frame.builder()
                                             .sequenceId(sequenceId)
                                             .type(FrameType.PRIVATE_CHAT_FAIL_RESPONSE.getType())
                                             .senderId(senderId.toString())
                                             .receiverId(receiverId.toString()).build();
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
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //ChatMessageHandler.clients.add(ctx.channel());

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        AttributeKey<Object> attributeKey = AttributeKey.valueOf("userId");
        Long userId = Long.valueOf(ctx.attr(attributeKey).get().toString());
        log.info("ChatMessageHandler#handlerRemoved: 用户[{}]连接断开,连接管理器移除连接", userId);
        //ChatMessageHandler.clients.remove(ctx.channel());
        UserChannelManager.remove(ctx.channel());
        ctx.channel().close();
        sendOffLineMessage(userId);
    }

    private static void sendOffLineMessage(Long userId) {
        UserService userService = (UserService) SpringBeanUtil.getBean(UserServiceImpl.class);
        User user = userService.getBaseMapper().selectById(userId);
        FriendService friendService = (FriendService) SpringBeanUtil.getBean(FriendServiceImpl.class);
        List<User> allFriendList = friendService.getFriendList(userId);
        Iterator<User> iterator = allFriendList.iterator();
        UserInfo userInfo = UserMapStruct.USER_MAPPING.userToUserInfo(user);
        Frame offLinedFriendInfo = Frame.builder().type(FrameType.FRIEND_OFFLINE.getType())
                                        .senderId(userId.toString())
                                        .extend(ImmutableMap.of("offLinedFriendInfo", userInfo))
                                        .build();
        //TODO 向好友推送下线消息
        while(iterator.hasNext()) {
            User friend = iterator.next();
            //好友在线即进行推送更新
            if(UserChannelManager.contains(friend.getId())) {
                Channel channel = UserChannelManager.getChannel(friend.getId());
                offLinedFriendInfo.setReceiverId(friend.getId().toString());
                channel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(offLinedFriendInfo)));
            }
        }
    }

}
