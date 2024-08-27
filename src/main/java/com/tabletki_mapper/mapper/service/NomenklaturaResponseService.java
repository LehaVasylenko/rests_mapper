package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.nomenklatura.NomenklaturaResponseObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NomenklaturaResponseService {

    final ReactiveRedisTemplate<String, Nomenklatura> redis;
    final QueueService queueService;
    int counter = 0;

    public Mono<NomenklaturaResponseObject> processNomenklatura(Nomenklatura nomenklatura, String branchId, String username) {
        nomenklatura.setUsername(username);
        nomenklatura.setExternalPharmacyId(branchId);

        int offersCount = nomenklatura.getOffers() != null ? nomenklatura.getOffers().size() : 0;
        int suppliersCount = nomenklatura.getSuppliers() != null ? nomenklatura.getSuppliers().size() : 0;

        Mono<Integer> offersWithSupplierCodesCount = Mono.just(0);
        if (nomenklatura.getOffers() != null) {
            offersWithSupplierCodesCount = Flux.fromIterable(nomenklatura.getOffers())
                    .filter(offer -> offer.getSupplierCodes() != null && !offer.getSupplierCodes().isEmpty())
                    .count()
                    .map(Long::intValue);
        }

        Mono<NomenklaturaResponseObject> response = offersWithSupplierCodesCount
                .map(count -> NomenklaturaResponseObject.builder()
                        .isError(false)
                        .offersCount(offersCount)
                        .suppliersCount(suppliersCount)
                        .offersWithSupplierCodesCount(count)
                        .isSupplierError(false)
                        .build());

        return response.flatMap(res -> {
            String key = "nomenklatura_input_" + nomenklatura.getUsername() + "_" + this.counter+++ ":" + System.currentTimeMillis();
            return redis.opsForValue().set(key, nomenklatura)
                    .doOnSuccess(v -> queueService.getNomenklaturaQueue().add(key))
                    .flatMap(result -> {
                        if (result) {
                            // Установка времени жизни записи в Redis
                            return redis.expire(key, Duration.ofHours(1))
                                    .doOnSuccess(expired -> {
                                        if (expired) {
                                            queueService.getNomenklaturaQueue().add(key);
                                            log.info("Nomenklatura added with TTL: {}", nomenklatura.getOffers().size());
                                        }
                                    });
                        }
                        return Mono.just(false);
                    })
                    .thenReturn(res);
        });
    }
}
