package org.golive.im.core.server.handler.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import jakarta.annotation.Resource;
import org.golive.im.core.server.common.ChannelHandlerContextCache;
import org.golive.im.core.server.common.ImContextUtils;
import org.golive.im.core.server.common.ImMsg;
import org.golive.im.core.server.handler.ImHandlerFactory;
import org.golive.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * im消息统一handler入口
 */
@Component
@ChannelHandler.Sharable
public class WsImServerCoreHandler extends SimpleChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsImServerCoreHandler.class);

    @Resource
    private ImHandlerFactory imHandlerFactory;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof WebSocketFrame){
            wsMsgHandler(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 正常或者意外断线,都会回调到这里
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId != null && appId != null){
            ChannelHandlerContextCache.remove(userId);
            // 移除用户之前连接上的ip记录
            redisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
        }
    }

    private void wsMsgHandler(ChannelHandlerContext ctx, WebSocketFrame msg) {
        //如果不是文本消息，统一后台不会处理
        if (!(msg instanceof TextWebSocketFrame)) {
            LOGGER.error(String.format("[WebsocketCoreHandler]  wsMsgHandler , %s msg types not supported", msg.getClass().getName()));
            return;
        }
        try {
            // 返回应答消息
            String content = ((TextWebSocketFrame) msg).text();
            JSONObject jsonObject = JSON.parseObject(content, JSONObject.class);
            ImMsg imMsg = new ImMsg();
            imMsg.setMagic(jsonObject.getShort("magic"));
            imMsg.setCode(jsonObject.getInteger("code"));
            imMsg.setLen(jsonObject.getInteger("len"));
            imMsg.setBody(jsonObject.getString("body").getBytes());
            imHandlerFactory.doMsgHandler(ctx, imMsg);
        } catch (Exception e) {
            LOGGER.error("[WebsocketCoreHandler] wsMsgHandler error is:", e);
        }

    }
}
