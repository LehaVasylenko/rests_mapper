package com.tabletki_mapper.mapper.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurerComposite;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer
                .defaultCodecs()
                .maxInMemorySize(1024 * 1024 * 1024); // 16MB
    }

    @Bean
    public WebFluxConfigurer webFluxConfigurer() {
        return new WebFluxConfigurerComposite() {

            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer
                        .defaultCodecs()
                        .maxInMemorySize(1024 * 1024 * 1024);// 16MB
            }
        };
    }

    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(2000)  // количество подключений
                .pendingAcquireTimeout(Duration.ofSeconds(30))  //  тайм-аут
                .maxIdleTime(Duration.ofMinutes(1))  // Очистка неактивных соединений
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .compress(true)
                .keepAlive(true)
                .responseTimeout(Duration.ofMinutes(2))  //  тайм-аут ответа
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_RCVBUF, 2048)  //  размер буфера
                .option(ChannelOption.SO_SNDBUF, 2048);  //  размер буфера

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
