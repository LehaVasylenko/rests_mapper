package com.tabletki_mapper.mapper.config;

import com.tabletki_mapper.mapper.model.UserDB;
import com.tabletki_mapper.mapper.model.user.Role;
import com.tabletki_mapper.mapper.model.user.User;
import com.tabletki_mapper.mapper.repository.UserRepository;
import com.tabletki_mapper.mapper.service.ReactiveUserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class SecurityConfig {
    final UserRepository repository;
    final ReactiveRedisTemplate<String, UserDB> redisTemplate;

    String[] userPath = {"/Import/**"};
    String[] adminPath = {"/service/**"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveUserDetailsService userDetailsService) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(adminPath).hasAuthority(Role.ADMIN.name())
                .pathMatchers(userPath).hasAuthority(Role.SHOP.name())
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .httpBasic()
                .and()
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new ReactiveUserDetailsServiceImpl(repository, redisTemplate);
    }
}

