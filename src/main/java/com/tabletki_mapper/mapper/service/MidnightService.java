package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.dto.DrugEntityDTO;
import com.tabletki_mapper.mapper.mapper.DrugEntityMapper;
import com.tabletki_mapper.mapper.model.ShopPoint;
import com.tabletki_mapper.mapper.model.UserDB;
import com.tabletki_mapper.mapper.repository.DrugEntityRepository;
import com.tabletki_mapper.mapper.repository.ShopPointRepository;
import com.tabletki_mapper.mapper.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 19.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MidnightService {

    @Value("${redis.prefix.user}")
    private String userPrefix;

    @Value("${redis.prefix.nomenklatura}")
    private String drugPrefix;

    @Value("${redis.prefix.shop}")
    private String shopPrefix;

    final UserRepository userRepository;
    final DrugEntityRepository drugEntityRepository;
    final ShopPointRepository shopPointRepository;

    final ReactiveRedisTemplate<String, UserDB> userRedisTemplate;
    final ReactiveRedisTemplate<String, DrugEntityDTO> drugRedisTemplate;
    final ReactiveRedisTemplate<String, ShopPoint> shopRedisTemplate;

    @PostConstruct
    public void init() {
        shopPointRepository.findAll()
                .flatMap(shopPoint -> {
                    String cacheKey = shopPrefix + shopPoint.getCacheId();
                    return shopRedisTemplate.opsForValue().set(cacheKey, shopPoint);
                })
                .doOnComplete(() -> log.info("ShopPoint Cache initialized with data from database."))
                .subscribe();

        userRepository.findAll()
                .doOnNext(user -> userRedisTemplate.opsForValue()
                        .set(userPrefix + user.getUsername(), user).subscribe()) // Инициализируем кэш пользователей
                .then()
                .subscribe();

        drugEntityRepository.findAll()
                .flatMap(drugEntity -> {
                    String cacheKey = drugPrefix + drugEntity.getUserLogin() + ":" + drugEntity.getDrugId();
                    DrugEntityDTO drugEntityDTO = DrugEntityMapper.INSTANCE.toDto(drugEntity);
                    return drugRedisTemplate.opsForValue().set(cacheKey, drugEntityDTO)
                            .then(Mono.just(drugEntityDTO));
                })
                .doOnComplete(() -> log.info("Nomenklatura Cache initialized with data from database."))
                .subscribe();
    }

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void runMidnightJob() {
        log.info("Running midnight job...");
        saveIt();
    }

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        log.info("Application is shutting down...");
        saveIt();
    }

    private void saveIt() {
        Mono<Void> usersJob = userRedisTemplate.keys(userPrefix + "*")
                .flatMap(key -> userRedisTemplate.opsForValue().get(key))
                .collectList()
                .flatMapMany(userRepository::saveAll)
                .then()
                .doOnError(e -> log.error("Failed to save user from cache", e))
                .then();

        Mono<Void> drugsJob = drugRedisTemplate.keys(drugPrefix + "*")
                .flatMap(key -> drugRedisTemplate.opsForValue().get(key))
                .map(DrugEntityMapper.INSTANCE::toModel)
                .collectList()
                .flatMapMany(drugEntityRepository::saveAll)
                .then()
                .doOnError(e -> log.error("Failed to save drug from cache", e))
                .then();

        Mono<Void> shopsJob = shopRedisTemplate.keys(shopPrefix + "*")
                .flatMap(key -> shopRedisTemplate.opsForValue().get(key))
                .collectList()
                .flatMapMany(shopPointRepository::saveAll)
                .then()
                .doOnError(e -> log.error("Failed to save shop from cache", e))
                .then();

        Mono<Void> allJobs = Mono.when(usersJob, drugsJob, shopsJob);

        allJobs
                .doOnSuccess(v -> log.info("Midnight job completed successfully"))
                .doOnError(e -> log.error("Error during midnight job", e))
                .subscribe();
    }
}
