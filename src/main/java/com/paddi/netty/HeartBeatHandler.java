package com.paddi.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 15:12:06
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE) {
                HeartBeatHandler.log.info("进入了读空闲");
            } else if(event.state() == IdleState.WRITER_IDLE) {
                HeartBeatHandler.log.info("进入了写空闲");
            } else if(event.state() == IdleState.ALL_IDLE) {
                HeartBeatHandler.log.info("进入了读写空闲,即将关闭连接....");
                HeartBeatHandler.log.info("关闭连接前,当前连接数为：{}",ChatMessageHandler.clients.size());
                ctx.channel().close();
                HeartBeatHandler.log.info("关闭连接后,当前连接数为：{}",ChatMessageHandler.clients.size());
            }
        }
    }
}
