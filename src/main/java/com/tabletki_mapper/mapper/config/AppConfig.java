package com.tabletki_mapper.mapper.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import reactor.netty.resources.LoopResources;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 20.08.2024
 */
@Configuration
public class AppConfig {
    int core = Runtime.getRuntime().availableProcessors();

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(this.core / 2);
        scheduler.setThreadNamePrefix("scheduled-task-");
        return scheduler;
    }

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(httpServer -> httpServer.runOn(LoopResources.create(
                "http",
                this.core * 5,
                true)));
        return factory;
    }
}
