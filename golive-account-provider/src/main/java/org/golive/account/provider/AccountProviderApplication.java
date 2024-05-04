package org.golive.account.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.golive.account.provider.service.IAccountTokenService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class AccountProviderApplication{ //implements CommandLineRunner {

    // @Resource
    // private IAccountTokenService accountTokenService;

    public static void main(String[] args) {
        SpringApplication.run(AccountProviderApplication.class, args);
    }

    // @Override
    // public void run(String... args) throws Exception {
    //     Long userId = 1092813L;
    //     String token = accountTokenService.createAndSaveLoginToken(userId);
    //     System.out.println("token is : " + token);
    //     Long matchUserId = accountTokenService.getUserIdByToken(token);
    //     System.out.println("matchUserId is :" + matchUserId);
    // }
}
