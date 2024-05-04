package org.golive.im.router.provider;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.golive.im.dto.ImMsgBody;
import org.golive.im.router.provider.service.ImRouterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ImRouterProviderApplication{ // implements CommandLineRunner {

    // @Resource
    // private ImRouterService imRouterService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ImRouterProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    // @Override
    // public void run(String... args) throws Exception {
    //     for (int i = 0; i < 1000; i++) {
    //         ImMsgBody imMsgBody = new ImMsgBody();
    //         imRouterService.sendMsg(imMsgBody);
    //         Thread.sleep(1000 );
    //     }
    // }
}
