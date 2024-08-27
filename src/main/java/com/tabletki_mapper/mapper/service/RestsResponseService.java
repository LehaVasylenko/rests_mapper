package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.rests.Rests;
import com.tabletki_mapper.mapper.model.rests.RestsResponseObject;
import com.tabletki_mapper.mapper.model.rests.BranchResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestsResponseService {

    final ReactiveRedisTemplate<String, Rests> redis;
    final QueueService queueService;
    int counter = 0;

    public Mono<RestsResponseObject> processRests(Rests rests, String username) {
        rests.setUsername(username);

        return Flux.fromIterable(rests.getBranches())
                .map(branch -> BranchResult.builder()
                        .branchID(branch.getCode())
                        .restCount(branch.getRests().size())
                        .errorMessage("")
                        .isError(false)
                        .build())
                .collectList()
                .flatMap(branchResults -> {
                    RestsResponseObject response = RestsResponseObject.builder()
                            .branchResults(branchResults)
                            .errorMessage("")
                            .isError(false)
                            .build();

                    // Генерация ключа и сохранение в Redis
                    String key = "rests_input_" + rests.getUsername() + "_" + this.counter++ + ":" + System.currentTimeMillis();
                    return redis.opsForValue().set(key, rests)
                            .flatMap(result -> {
                                if (result) {
                                    // Установка времени жизни записи в Redis
                                    return redis.expire(key, Duration.ofHours(1))
                                            .doOnSuccess(expired -> {
                                                if (expired) {
                                                    queueService.getRestsQueue().add(key);
                                                    log.info("Rests added with TTL: {}", rests.getBranches().size());
                                                }
                                            });
                                }
                                return Mono.just(false);
                            })
                            .thenReturn(response);
                });
    }
}
