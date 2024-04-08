package org.golive.framework.datasource.starter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class ShardingJdbcDatasourceAutoInitConnectionConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShardingJdbcDatasourceAutoInitConnectionConfig.class);

    @Bean
    public ApplicationRunner runner(DataSource dataSource){
        /*
        * SpringBoot3.0之后,Hikari数据库无法做到在容器启动前
        * 自动对shardingJDBC的连接池进行初始化,需要手动触发一下
        * */
        return args -> {
            LOGGER.info("dataSource:{}", dataSource);
            // 手动触发连接池的连接创建
            Connection connection = dataSource.getConnection();
        };
    }
}
