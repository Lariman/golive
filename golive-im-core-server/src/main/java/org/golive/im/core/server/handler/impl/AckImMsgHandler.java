package org.golive.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.golive.im.core.server.common.ImContextUtils;
import org.golive.im.core.server.common.ImMsg;
import org.golive.im.core.server.handler.SimplyHandler;
import org.golive.im.core.server.service.IMsgAckCheckService;
import org.golive.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 登出消息的处理逻辑
 */
@Component
public class AckImMsgHandler implements SimplyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginMsgHandler.class);
    @Resource
    private IMsgAckCheckService msgAckCheckService;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId == null && appId == null){
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        msgAckCheckService.doMsgAck(JSON.parseObject(imMsg.getBody(), ImMsgBody.class));
    }
}
