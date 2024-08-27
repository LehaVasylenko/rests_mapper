package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.model.UserDB;
import com.tabletki_mapper.mapper.model.user.User;
import com.tabletki_mapper.mapper.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    final UserRepository repository;
    final ReactiveRedisTemplate<String, UserDB> redisTemplate;

    @Value("${redis.prefix.user}")
    String userPrefix;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return redisTemplate.opsForValue().get(userPrefix + username)
                .doOnNext(userDb -> log.info("Redis: {}", username)) // Логирование при чтении из Redis
                .map(this::convertToUserDetails)
                .switchIfEmpty(
                        repository.findByUsername(username)
                                .doOnNext(userDb -> {
                                    log.info("Database: {}", username); // Логирование при чтении из базы
                                    redisTemplate.opsForValue().set(userPrefix + username, userDb).subscribe();
                                })
                                .map(this::convertToUserDetails)
                );
    }

    private UserDetails convertToUserDetails(UserDB userDb) {
        return User.getUser(userDb);
    }

}
