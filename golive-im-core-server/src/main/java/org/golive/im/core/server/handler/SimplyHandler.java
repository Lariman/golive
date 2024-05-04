package org.golive.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.golive.im.core.server.common.ImMsg;

public interface SimplyHandler {

    /**
     * 消息处理函数
     * @param ctx
     * @param imMsg
     */
    void handler(ChannelHandlerContext ctx, ImMsg imMsg);
}
