package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.dto.DrugEntityDTO;
import com.tabletki_mapper.mapper.mapper.DrugEntityMapper;
import com.tabletki_mapper.mapper.model.DrugEntity;
import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.nomenklatura.Offers;
import com.tabletki_mapper.mapper.model.nomenklatura.SupplierCode;
import com.tabletki_mapper.mapper.repository.DrugEntityRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * reactive
 * Author: Vasylenko Oleksii
 * Date: 17.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NomenklaturaService {

    final UuidService uuidService;

    final DrugEntityRepository drugEntityRepository;
    final ReactiveRedisTemplate<String, DrugEntityDTO> reactiveRedisTemplate;
    int counter = 0;

    @Value("${redis.prefix.nomenklatura}")
    String nomPrefix;

    @Transactional
    public Mono<Void> processNomenklatura(Nomenklatura nomenklatura) {
        this.counter++;
        String userLogin = nomenklatura.getUsername();
        log.info("Processing: {}: {} -> {}", this.counter, userLogin, nomenklatura.getOffers().size());

        return Flux.fromIterable(nomenklatura.getOffers())
                .flatMap(offer -> {
                    DrugEntityDTO newDrugEntity = DrugEntityDTO.builder()
                            .id(uuidService.generateUniqueUUID())
                            .userLogin(userLogin)
                            .drugId(offer.getCode())
                            .drugName(offer.getName())
                            .drugProducer(offer.getProducer())
                            .morionId(getId(offer, "Morion"))
                            .optimaId(getId(offer, "Optima"))
                            .barcode(getId(offer, "Barcode"))
                            .home(offer.getHome())
                            .pfactor(offer.getPfactor())
                            .build();

                    return getDrugFromCacheOrDB(userLogin, offer.getCode())
                            .flatMap(existingDrugEntity -> {
                                if (existingDrugEntity != null && !existingDrugEntity.equals(newDrugEntity)) {
                                    // Обновление существующей записи
                                    existingDrugEntity.setDrugName(newDrugEntity.getDrugName());
                                    existingDrugEntity.setDrugProducer(newDrugEntity.getDrugProducer());
                                    existingDrugEntity.setMorionId(newDrugEntity.getMorionId());
                                    existingDrugEntity.setOptimaId(newDrugEntity.getOptimaId());
                                    existingDrugEntity.setBarcode(newDrugEntity.getBarcode());
                                    return persistToDatabase(userLogin,
                                            List.of(DrugEntityMapper.INSTANCE.toModel(existingDrugEntity)),
                                            Collections.emptyList());
                                } else {
                                    // Добавление новой записи
                                    return persistToDatabase(userLogin,
                                            Collections.emptyList(),
                                            List.of(DrugEntityMapper.INSTANCE.toModel(newDrugEntity)));
                                }
                            });
                })
                .then(Mono.defer(Mono::empty));
    }

    private Mono<DrugEntityDTO> getDrugFromCacheOrDB(String userLogin, String drugId) {
        String cacheKey = nomPrefix + userLogin + ":" + drugId;
        return reactiveRedisTemplate.opsForValue()
                .get(cacheKey)
                .switchIfEmpty(drugEntityRepository.findByDrugIdAndUserLogin(drugId, userLogin)
                        .map(DrugEntityMapper.INSTANCE::toDto)
                        .doOnNext(drug -> {
                            if (drug != null) {
                                // Сохранение в кеш
                                reactiveRedisTemplate.opsForValue()
                                        .set(cacheKey, drug)
                                        .doOnSuccess(aVoid -> log.debug("Added drug {} to cache", drug.getCacheKey()))
                                        .subscribe();
                            }
                        })
                        .defaultIfEmpty(new DrugEntityDTO())) // Если в БД тоже нет, вернуть пустой объект
                .doOnNext(drug -> log.debug("Retrieved drug {} from cache or DB", drug.getDrugName()));
    }

    private Mono<Void> persistToDatabase(String userLogin, List<DrugEntity> drugsToUpdate, List<DrugEntity> drugsToInsert) {
        if (shouldPersistToDatabase()) {
            return drugEntityRepository.saveAll(Flux.fromIterable(drugsToUpdate).concatWith(Flux.fromIterable(drugsToInsert)))
                    .doOnComplete(() -> log.info("Persisted {} records to database for user: {}", drugsToUpdate.size() + drugsToInsert.size(), userLogin))
                    .then();
        } else {
            return Mono.empty();
        }
    }

    private boolean shouldPersistToDatabase() {
        if (this.counter >= 500) {
            this.counter = 0;
            return true;
        }
        return false;
    }

    private String getId(Offers offer, String target) {
        return offer.getSupplierCodes().stream()
                .filter(supplierCode -> target.equals(supplierCode.getId()))
                .map(SupplierCode::getCode)
                .findFirst()
                .orElse("");
    }
}

