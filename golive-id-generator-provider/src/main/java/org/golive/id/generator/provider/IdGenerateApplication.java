package org.golive.id.generator.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.golive.id.generator.provider.service.IdGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.HashSet;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class IdGenerateApplication{// implements CommandLineRunner{

    // private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerateApplication.class);

    // @Resource
    // private IdGenerateService idGenerateService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IdGenerateApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
    // @Override
    // public void run(String... args) throws Exception {
    //     HashSet<Long> idSet = new HashSet<>();
    //     for(int i = 0; i < 1300; i++){
    //         Long id = idGenerateService.getUnSeqId(1);
    //         idSet.add(id);
    //
    //         System.out.println(id);
    //     }
    //     System.out.println(idSet.size());
    // }
}
