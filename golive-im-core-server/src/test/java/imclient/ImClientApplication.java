package imclient;
// package imclient;
//
// import imclient.handler.ClientHandler;
// import io.netty.bootstrap.Bootstrap;
// import io.netty.channel.Channel;
// import io.netty.channel.ChannelFuture;
// import io.netty.channel.ChannelInitializer;
// import io.netty.channel.EventLoopGroup;
// import io.netty.channel.nio.NioEventLoopGroup;
// import io.netty.channel.socket.SocketChannel;
// import io.netty.channel.socket.nio.NioSocketChannel;
// import org.golive.im.core.server.common.ImMsg;
// import org.golive.im.core.server.common.ImMsgDecoder;
// import org.golive.im.core.server.common.ImMsgEncoder;
// import org.golive.im.constants.ImMsgCodeEnum;
//
// public class ImClientApplication {
//
//     private void startConnection(String address, int port) throws InterruptedException{
//         //客户端需要一个事件循环组
//         EventLoopGroup clientGroup = new NioEventLoopGroup();
//         //创建客户端启动对象
//         Bootstrap bootstrap = new Bootstrap();
//         //设置相关参数
//         bootstrap.group(clientGroup);
//         //设置客户端通道的实现类(反射)
//         bootstrap.channel(NioSocketChannel.class);
//         bootstrap.handler(new ChannelInitializer<SocketChannel>() {
//             @Override
//             protected void initChannel(SocketChannel ch) throws Exception {
//                 System.out.println("初始化连接建立");
//                 ch.pipeline().addLast(new ImMsgEncoder());
//                 ch.pipeline().addLast(new ImMsgDecoder());
//                 ch.pipeline().addLast(new ClientHandler());
//             }
//         });
//         //启动客户端去连接服务器端
//         ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
//         //给关闭通道进行监听
//         Channel channel = channelFuture.channel();
//         for(int i = 0; i < 100; i++){
//             channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), "login"));
//             channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), "logout"));
//             channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), "biz"));
//             channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), "heart"));
//             Thread.sleep(3000);
//         }
//     }
//
//     public static void main(String[] args) throws InterruptedException {
//         ImClientApplication client = new ImClientApplication();
//         client.startConnection("localhost", 9090);
//     }
// }

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

@SpringBootApplication
@EnableDubbo
public class ImClientApplication{
    public static void main(String[] args) {
        // ApplicationHome home = new ApplicationHome(ImClientApplication.class);
        // File jarFile = home.getSource();
        // String dirPath = jarFile.getParentFile().toString();
        // String filePath = dirPath + File.separator + ".dubbo";
        SpringApplication springApplication = new SpringApplication(ImClientApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}