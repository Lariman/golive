package org.golive.im.core.server.service.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import istio.v1.auth.Ca;
import jakarta.annotation.Resource;
import org.golive.im.constants.ImMsgCodeEnum;
import org.golive.im.core.server.common.ChannelHandlerContextCache;
import org.golive.im.core.server.common.ImMsg;
import org.golive.im.core.server.service.IMsgAckCheckService;
import org.golive.im.core.server.service.IRouterHandlerService;
import org.golive.im.dto.ImMsgBody;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RouterHandlerServiceImpl implements IRouterHandlerService {

    @Resource
    private IMsgAckCheckService msgAckCheckService;

    @Override
    public void onReceive(ImMsgBody imMsgBody) {
        // 需要进行消息通知的userId
        if(sendMsgToClient(imMsgBody)){
            // 当im服务器推送了消息给到客户端,然后需要记录下ack
            msgAckCheckService.recordMsgAck(imMsgBody, 1);
            msgAckCheckService.sendDelayMsg(imMsgBody);
        }
    }

    @Override
    public boolean sendMsgToClient(ImMsgBody imMsgBody) {
        // 需要进行消息通知的userId
        Long userId = imMsgBody.getUserId();
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        if(ctx != null){
            String msgId = UUID.randomUUID().toString();
            imMsgBody.setMsgId(msgId);
            ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBody));
            ctx.writeAndFlush(respMsg);
            return true;
        }
        return false;
    }

}
