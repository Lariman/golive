package imclient.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.golive.im.constants.ImMsgCodeEnum;
import org.golive.im.core.server.common.ImMsg;
import org.golive.im.dto.ImMsgBody;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    //当通道有读取事件时，会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImMsg imMsg = (ImMsg) msg;
        if(imMsg.getCode() == ImMsgCodeEnum.IM_BIZ_MSG.getCode()){
            ImMsgBody respBody = JSON.parseObject(new String(imMsg.getBody()), ImMsgBody.class);
            ImMsgBody ackBody = new ImMsgBody();
            ackBody.setMsgId(respBody.getMsgId());
            ackBody.setAppId(respBody.getAppId());
            ackBody.setData(respBody.getData());
            ackBody.setUserId(respBody.getUserId());
            ImMsg ackMsg = ImMsg.build(ImMsgCodeEnum.IM_ACK_MSG.getCode(), JSON.toJSONString(ackBody));
            ctx.writeAndFlush(ackMsg);
        }
        System.out.println("【服务端响应数据】 result is :" + imMsg);
    }
}
