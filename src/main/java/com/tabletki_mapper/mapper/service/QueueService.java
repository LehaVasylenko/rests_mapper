package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.rests.Rests;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 19.08.2024
 */
@Slf4j
@Service
@Data
@EnableScheduling
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QueueService {

    final NomenklaturaService nomenklaturaService;
    final RestsService restsService;
    final ReactiveRedisTemplate<String, Rests> redisRests;
    final ReactiveRedisTemplate<String, Nomenklatura> redisNomenklatura;

    Queue<String> nomenklaturaQueue = new ConcurrentLinkedQueue<>();
    Queue<String> restsQueue = new ConcurrentLinkedQueue<>();

    int threads = Runtime.getRuntime().availableProcessors() / 2;
    ExecutorService executorService = Executors.newFixedThreadPool(threads);
    Scheduler schedulerNom = Schedulers.fromExecutor(executorService);

    ExecutorService executorService2 = Executors.newFixedThreadPool(threads);
    Scheduler schedulerRests = Schedulers.fromExecutor(executorService2);

    // Метод для обработки очереди Nomenklatura
    @Scheduled(fixedDelay = 25)
    public void processNomenklaturaQueue() {
        String key = nomenklaturaQueue.poll(); // Забираем ключ из очереди

        if (key != null) {
            redisNomenklatura.opsForValue().get(key)  // Получаем объект из Redis
                    .flatMap(nomenklaturaService::processNomenklatura)
                    .publishOn(schedulerNom)
                    .subscribe(
                            result -> log.info("Successfully processed Nomenklatura with key: {}", key),
                            error -> log.error("Error processing Nomenklatura: {}", error.getMessage())
                    );
        }
    }

    @Scheduled(fixedDelay = 1500)
    public void processRestsQueue() {
        if (restsService.getActiveCount().get() < threads) {
            String key = restsQueue.poll(); // Забираем ключ из очереди

            if (key != null) {
                log.warn("Rests Queue size: {}", restsQueue.size());

                // Запускаем все операции на пользовательском Scheduler
                Mono.defer(() -> redisRests.opsForValue().get(key)) // Получаем объект из Redis
                        .subscribeOn(schedulerRests) // Выполняем в управляющих потоках
                        .flatMap(restsService::processRests)
                        .log()
                        .subscribe(
                                result -> log.info("Successfully processed Rests with key: {}", key),
                                error -> log.error("Error processing Rests: {}", error.getMessage())
                        );
            }
        }
    }
}

