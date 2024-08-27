package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.dto.DrugEntityDTO;
import com.tabletki_mapper.mapper.mapper.DrugEntityMapper;
import com.tabletki_mapper.mapper.model.GeoaptekaDataModel;
import com.tabletki_mapper.mapper.model.UserDB;
import com.tabletki_mapper.mapper.model.UserData;
import com.tabletki_mapper.mapper.model.rests.Branch;
import com.tabletki_mapper.mapper.model.rests.Rests;
import com.tabletki_mapper.mapper.repository.DrugEntityRepository;
import com.tabletki_mapper.mapper.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * reactive
 * Author: Vasylenko Oleksii
 * Date: 17.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestsService {

    final DrugEntityRepository repository;
    final UserRepository userRepository;
    final PrepareService service;
    final ReactiveRedisTemplate<String, DrugEntityDTO> reactiveRedisTemplate;
    final ReactiveRedisTemplate<String, UserDB> redisTemplate;

    @Value("${redis.prefix.nomenklatura}")
    String nomPrefix;

    @Value("${redis.prefix.user}")
    String userPrefix;

    @Getter
    AtomicInteger activeCount = new AtomicInteger(0); // Счетчик активных вызовов

    public Mono<Void> processRests(Rests message) {
        log.warn("{}: Start processing Rests: {} -> {} drugstores", Thread.currentThread().getName(), message.getUsername(), message.getBranches().size());
        // Увеличиваем счетчик
        int currentCount = activeCount.incrementAndGet();
        log.info("Active count before processing: {}", currentCount);

        // Проверяем кэш на наличие пользователя
        return redisTemplate.opsForValue().get(userPrefix + message.getUsername())
                .switchIfEmpty(userRepository.findByUsername(message.getUsername()) // Если в кэше нет, идем в репозиторий
                        .doOnNext(user -> redisTemplate.opsForValue().set(userPrefix + message.getUsername(), user).subscribe())) // Кладем в кэш
                .flatMap(user ->
                        Flux.fromIterable(message.getBranches())
                                .flatMap(branch -> processBranch(branch, user)) // Обрабатываем каждый branch
                                .collectList() // Собираем все результаты в список
                                .flatMap(service::prepateData) // Отправляем список в сервис
                                .doOnSuccess(result -> {
                                    // Уменьшаем счетчик сразу после отправки данных в сервис
                                    int remainingCount = activeCount.decrementAndGet();
                                    log.info("Active count after sending data: {}", remainingCount);
                                })
                )
                .then(); // Завершаем Mono<Void>
    }

    private Mono<DrugEntityDTO> getDrugFromCacheOrDB(String username, String drugId) {
        String cacheKey = nomPrefix + username + ":" + drugId;

        return reactiveRedisTemplate.opsForValue().get(cacheKey) // Попытка получить объект из Redis
                .switchIfEmpty(repository.findByDrugIdAndUserLogin(drugId, username) // Если нет в Redis, получаем из базы данных
                        .map(DrugEntityMapper.INSTANCE::toDto) // Преобразуем в DTO
                        .doOnNext(drugDTO -> {
                            // Сохраняем объект в Redis
                            reactiveRedisTemplate.opsForValue().set(cacheKey, drugDTO).subscribe();
                        })
                        .defaultIfEmpty(new DrugEntityDTO())) // Если объект не найден в базе данных, возвращаем пустой объект
                .doOnNext(drug -> log.debug("Retrieved drug {} from cache", drug.getDrugName()));
    }

    private Mono<UserData> processBranch(Branch branch, UserDB user) {
        //log.warn("Processing branch: {}", branch.getCode());
        return Flux.fromIterable(branch.getRests())
                .flatMap(rest -> getDrugFromCacheOrDB(user.getUsername(), rest.getCode())
                        .map(drug -> GeoaptekaDataModel.builder()
                                .id(rest.getCode())
                                .name(drug != null && drug.getMorionDescription() != null ? drug.getMorionDescription() : "No data received")
                                .price(rest.getPrice() != null ? rest.getPrice() : 0)
                                .quant(rest.getQty() != null ? rest.getQty() : 0)
                                .priceCntr(rest.getPriceReserve() != null ? rest.getPriceReserve() : 0)
                                .home(drug != null && drug.getHome() != null ? drug.getHome() : "")
                                .pfactor(drug != null && drug.getPfactor() != null ? drug.getPfactor() : 1)
                                .build())
                        .doOnNext(dataModel -> log.debug("Processed GeoaptekaDataModel: {}", dataModel.getName()))
                )
                .collectList() // Собираем все GeoaptekaDataModel в список
                .map(geoaptekaDataModels -> UserData.builder()
                        .user(user)
                        .shopExtId(branch.getCode())
                        .payload(geoaptekaDataModels)
                        .build()); // Создаем UserData
    }
}

