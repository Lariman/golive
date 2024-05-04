package org.golive.msg.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.golive.msg.dto.MsgCheckDTO;
import org.golive.msg.enums.MsgSendResultEnum;
import org.golive.msg.provider.service.ISmsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Scanner;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class MsgProviderApplication { // implements CommandLineRunner{

    // @Resource
    // private ISmsService smsService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MsgProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    // @Override
    // public void run(String... args) throws Exception {
    //     MsgSendResultEnum msgSendResultEnum = smsService.sendLoginCode("15888888888");
    //     System.out.println("发送结果:" + msgSendResultEnum);
    // }
}
