package org.golive.user.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.golive.user.constants.UserTagsEnum;
import org.golive.user.dto.UserDTO;
import org.golive.user.dto.UserLoginDTO;
import org.golive.user.interfaces.IUserPhoneRpc;
import org.golive.user.provider.service.IUserPhoneService;
import org.golive.user.provider.service.IUserService;
import org.golive.user.provider.service.IUserTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class UserProviderApplication{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProviderApplication.class);

    // @Resource
    // private IUserTagService userTagService;
    // @Resource
    // private IUserService userService;
    // @Resource
    // private IUserPhoneService userPhoneService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    // @Override
    // public void run(String... args) throws Exception {
        // ===================测试三:用户手机号登录============================
        // String phone = "16345678910";  // 模拟登录
        // UserLoginDTO userLoginDTO = userPhoneService.login(phone);
        // System.out.println(userLoginDTO);
        // System.out.println(userPhoneService.queryByUserId(userLoginDTO.getUserId()));
        // System.out.println(userPhoneService.queryByUserId(userLoginDTO.getUserId()));
        // System.out.println(userPhoneService.queryByPhone(phone));
        // System.out.println(userPhoneService.queryByPhone(phone));

        // // ===================测试三:用户标签============================
        // Long userId = 1004L;
        // UserDTO userDTO = userService.getByUserId(userId);  // 查询
        // userDTO.setNickName("test-nick_name");
        // userService.updateUserInfo(userDTO);
        //
        // System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
        // System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
        // System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
        // System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_OLD_USER));
        // System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));


        // ===================测试二:用户标签============================
        // Long userId = 1001L;
        // CountDownLatch count = new CountDownLatch(1);
        // for(int i = 0; i < 100; i++){
        //     Thread t1 = new Thread(new Runnable() {
        //         @Override
        //         public void run() {
        //             try{
        //                 count.await();
        //                 LOGGER.info("result is " + userTagService.setTag(userId, UserTagsEnum.IS_VIP));
        //             }catch (InterruptedException e){
        //                 throw new RuntimeException(e);
        //             }
        //         }
        //     });
        //     t1.start();
        // }
        // count.countDown();
        // Thread.sleep(100000);
        // ===================测试二============================


        // ===================测试一:用户标签============================
        // long userId = 1001L;
        // System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_VIP));
        // System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_VIP));
        // System.out.println("当前用户是否拥有 is_rich标签:" + userTagService.containTag(userId, UserTagsEnum.IS_RICH));
        // System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_VIP));
        // System.out.println("当前用户是否拥有 is_vip标签:" + userTagService.containTag(userId, UserTagsEnum.IS_VIP));
        // System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
        // System.out.println("当前用户是否拥有 is_old_user标签:" + userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
        //
        // System.out.println("=============================================");
        //
        // System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_VIP));
        // System.out.println("当前用户是否拥有 is_rich标签:" + userTagService.containTag(userId, UserTagsEnum.IS_RICH));
        // System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_VIP));
        // System.out.println("当前用户是否拥有 is_vip标签:" + userTagService.containTag(userId, UserTagsEnum.IS_VIP));
        // System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_OLD_USER));
        // System.out.println("当前用户是否拥有 is_old_user标签:" + userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
        // ===================测试一============================
    // }
}
