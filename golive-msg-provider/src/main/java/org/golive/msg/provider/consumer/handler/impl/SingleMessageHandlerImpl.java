package org.golive.msg.provider.consumer.handler.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.im.constants.AppIdEnum;
import org.golive.im.dto.ImMsgBody;
import org.golive.im.router.interfaces.rpc.ImRouterRpc;
import org.golive.living.interfaces.dto.LivingRoomReqDTO;
import org.golive.living.interfaces.rpc.ILivingRoomRpc;
import org.golive.msg.dto.MessageDTO;
import org.golive.im.router.interfaces.constants.ImMsgBizCodeEnum;
import org.golive.msg.provider.consumer.handler.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SingleMessageHandlerImpl implements MessageHandler {

    @DubboReference
    private ImRouterRpc routerRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @Override
    public void onMsgReceive(ImMsgBody imMsgBody) {
        int bizCode = imMsgBody.getBizCode();
        // 直播间的聊天消息
        if(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode() == bizCode){
            // 一个人发送,n个人接收
            // 根据roomId,appId去调用rpc方法,获取对应的直播间内的userId
            // 创建一个list的imMsgBody对象
            MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            Integer roomId = messageDTO.getRoomId();
            LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
            reqDTO.setRoomId(roomId);
            reqDTO.setAppId(imMsgBody.getAppId());
            List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO).stream().filter(x -> !x.equals(imMsgBody.getUserId())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(userIdList)){
                return;
            }
            List<ImMsgBody> imMsgBodies = new ArrayList<>();
            userIdList.forEach(userId->{
                ImMsgBody respMsg = new ImMsgBody();
                respMsg.setUserId(userId);
                respMsg.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
                respMsg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
                respMsg.setData(JSON.toJSONString(messageDTO));
                imMsgBodies.add(respMsg);
            });
            routerRpc.batchSendMsg(imMsgBodies);
        }
    }
}
