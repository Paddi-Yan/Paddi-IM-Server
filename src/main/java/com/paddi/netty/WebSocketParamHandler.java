package com.paddi.netty;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月27日 19:41:19
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketParamHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * 对URL进行提取参数并重定向URL 访问WebSocket的URL是不支持带参的
     * 先提取参数再将处理过后的URL放入通道中
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param request           the message to handle
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        WebSocketParamHandler.log.info("WebSocketParamHandler.channelRead0拦截到URL: {}", uri);
        if(!uri.startsWith("/ws?userId=")) {
            log.warn("WebSocketParamHandler.channelRead0拦截到恶意请求: {}", uri);
            return;
        }
        Map<CharSequence, CharSequence> queryMap = UrlBuilder.ofHttp(uri).getQuery().getQueryMap();
        //取出URL中的userId参数
        AttributeKey<String> attributeKey = AttributeKey.valueOf("userId");
        //放入通道传递到下一个处理器
        ctx.channel().attr(attributeKey).setIfAbsent(queryMap.get("userId").toString());
        request.setUri(URLUtil.getPath(uri));
        ctx.fireChannelRead(request.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
