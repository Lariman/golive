package imclient.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.im.constants.AppIdEnum;
import org.golive.im.constants.ImMsgCodeEnum;
import org.golive.im.core.server.common.*;
import org.golive.im.dto.ImMsgBody;
import org.golive.im.interfaces.ImTokenRpc;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Service
public class imClientHandler implements InitializingBean {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //客户端需要一个事件循环组
                EventLoopGroup clientGroup = new NioEventLoopGroup();
                //创建客户端启动对象
                Bootstrap bootstrap = new Bootstrap();
                //设置相关参数
                bootstrap.group(clientGroup);
                //设置客户端通道的实现类(反射)
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("初始化连接建立");
                        ch.pipeline().addLast(new TcpImMsgEncoder());
                        ch.pipeline().addLast(new TcpImMsgDecoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });

                //启动客户端去连接服务器端
                ChannelFuture channelFuture = null;
                try {
                    channelFuture = bootstrap.connect("192.168.0.3", 8085).sync();
                    Channel channel = channelFuture.channel();
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("请输入userId:");
                    Long userId = scanner.nextLong();
                    System.out.println("请输入objectId");
                    Long objectId = scanner.nextLong();
                    String token = imTokenRpc.createImLoginToken(userId, AppIdEnum.GOLIVE_BIZ.getCode());

                    // 发送登录消息体
                    ImMsgBody imMsgBody = new ImMsgBody();
                    imMsgBody.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
                    imMsgBody.setToken(token);
                    imMsgBody.setUserId(userId);
                    ImMsg loginMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBody));
                    channel.writeAndFlush(loginMsg);
                    // 心跳包机制
                    sendHeartBeat(userId, channel);

                    while (true) {
                        System.out.println("请输入聊天内容");
                        String content = scanner.nextLine();
                        ImMsgBody bizBody = new ImMsgBody();
                        bizBody.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
                        bizBody.setUserId(userId);
                        bizBody.setBizCode(5555);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", userId);
                        jsonObject.put("objectId", objectId);
                        jsonObject.put("content", content);
                        bizBody.setData(JSON.toJSONString(jsonObject));
                        ImMsg heartBeatMsg = ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(bizBody));
                        channel.writeAndFlush(heartBeatMsg);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        clientThread.start();
    // @Override
    // public void afterPropertiesSet() throws Exception {
    //     Thread clientThread = new Thread(new Runnable() {
    //         @Override
    //         public void run() {
    //             //客户端需要一个事件循环组
    //             EventLoopGroup clientGroup = new NioEventLoopGroup();
    //             //创建客户端启动对象
    //             Bootstrap bootstrap = new Bootstrap();
    //             //设置相关参数
    //             bootstrap.group(clientGroup);
    //             //设置客户端通道的实现类(反射)
    //             bootstrap.channel(NioSocketChannel.class);
    //             bootstrap.handler(new ChannelInitializer<SocketChannel>() {
    //                 @Override
    //                 protected void initChannel(SocketChannel ch) throws Exception {
    //                     System.out.println("初始化连接建立");
    //                     ch.pipeline().addLast(new ImMsgEncoder());
    //                     ch.pipeline().addLast(new ImMsgDecoder());
    //                     ch.pipeline().addLast(new ClientHandler());
    //                 }
    //             });
    //             Map<Long, Channel> userIdChannelMap = new HashMap<>();
    //             for(int i = 0; i < 10; i++){
    //                 Long userId = 200000L + i;
    //                 String token = imTokenRpc.createImLoginToken(userId, AppIdEnum.GOLIVE_BIZ.getCode());
    //                 //启动客户端去连接服务器端
    //                 ChannelFuture channelFuture = null;
    //                 try {
    //                     channelFuture = bootstrap.connect("localhost", 8085).sync();
    //                 } catch (InterruptedException e) {
    //                     throw new RuntimeException(e);
    //                 }
    //                 //给关闭通道进行监听
    //                 Channel channel = channelFuture.channel();
    //                 ImMsgBody imMsgBody = new ImMsgBody();
    //                 imMsgBody.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
    //                 imMsgBody.setToken(token);
    //                 imMsgBody.setUserId(userId);
    //                 ImMsg loginMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBody));
    //                 channel.writeAndFlush(loginMsg);
    //                 userIdChannelMap.put(userId, channel);
    //             }
    //             while(true){
    //                 for(Long userId: userIdChannelMap.keySet()){
    //                     // ImMsgBody heartBeatBody = new ImMsgBody();
    //                     // heartBeatBody.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
    //                     // heartBeatBody.setUserId(userId);
    //                     ImMsgBody bizBody = new ImMsgBody();
    //                     bizBody.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
    //                     bizBody.setUserId(userId);
    //                     JSONObject jsonObject = new JSONObject();
    //                     jsonObject.put("userId", userId);
    //                     jsonObject.put("objectId", 1001101L);
    //                     jsonObject.put("content", "你好,我是" + userId);
    //                     bizBody.setData(JSON.toJSONString(jsonObject));
    //                     ImMsg heartBeatMsg = ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(bizBody));
    //                     userIdChannelMap.get(userId).writeAndFlush(heartBeatMsg);
    //                 }
    //                 try {
    //                     Thread.sleep(1000);
    //                 } catch (InterruptedException e) {
    //                     throw new RuntimeException(e);
    //                 }
    //             }
    //         }
    //     });
    //     clientThread.start();
    }

    private void sendHeartBeat(Long userId, Channel channel) {
        new Thread(() -> {
            while(true){
                try{
                    Thread.sleep(30000);
                } catch(InterruptedException e){
                    throw new RuntimeException(e);
                }
                ImMsgBody imMsgBody = new ImMsgBody();
                imMsgBody.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
                imMsgBody.setUserId(userId);
                ImMsg loginMsg = ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(imMsgBody));
                channel.writeAndFlush(loginMsg);
            }
        }).start();
    }
}
