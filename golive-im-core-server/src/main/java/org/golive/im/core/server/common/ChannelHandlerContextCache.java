package org.golive.im.core.server.common;


import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class ChannelHandlerContextCache {

    /**
     * 当前的im服务启动的时候,对外暴露的ip和端口
      */
    private static String SERVER_IP_ADDRESS = "";

    private static Map<Long, ChannelHandlerContext> channelHandlerContextMap = new HashMap<>();

    public static String getServerIpAddress() {
        return SERVER_IP_ADDRESS;
    }

    public static void setServerIpAddress(String serverIpAddress) {
        SERVER_IP_ADDRESS = serverIpAddress;
    }

    public static Map<Long, ChannelHandlerContext> getChannelHandlerContextMap() {
        return channelHandlerContextMap;
    }

    public static void setChannelHandlerContextMap(Map<Long, ChannelHandlerContext> channelHandlerContextMap) {
        ChannelHandlerContextCache.channelHandlerContextMap = channelHandlerContextMap;
    }

    public static ChannelHandlerContext get(Long userId){
        return channelHandlerContextMap.get(userId);
    }

    public static void put(Long userId, ChannelHandlerContext channelHandlerContext){
        channelHandlerContextMap.put(userId, channelHandlerContext);
    }

    public static void remove(Long userId){
        channelHandlerContextMap.remove(userId);
    }

}
