package org.golive.im.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ImProviderApplication{// implements CommandLineRunner {

    // @Resource
    // private ImTokenService imTokenService;
    // @Resource
    // private ImOnlineService onlineService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ImProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    // @Override
    // public void run(String... args) throws Exception {
    //     for (int i = 0; i < 10; i++) {
    //         boolean online = onlineService.isOnline(1001L + i, AppIdEnum.GOLIVE_BIZ.getCode());
    //         System.out.println(online);
    //     }
    // }

    // @Override
    // public void run(String... args) throws Exception {
    //     long userId = 10921312L;
    //     String token = imTokenService.createImLoginToken(userId, AppIdEnum.GOLIVE_BIZ.getCode());
    //     System.out.println("token is :" + token);
    //     Long userIdResult = imTokenService.getUserIdByToken(token);
    //     System.out.println("userIdResult is : " + userIdResult);
    // }
}
