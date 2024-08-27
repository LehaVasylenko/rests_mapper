package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.repository.DrugEntityRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 19.08.2024
 */
@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UuidService {

    Set<UUID> uuidSet = Collections.synchronizedSet(new HashSet<>());
    final DrugEntityRepository repository;

    public UUID generateUniqueUUID() {
        UUID newUUID;
        do {
            newUUID = UUID.randomUUID();
        } while (!uuidSet.add(newUUID));

        return newUUID;
    }

    @PostConstruct
    void init() {
        repository.findAllDrugIds()
                .collectList()
                .doOnNext(uuidSet::addAll)
                .doOnError(e -> log.error("Error occurred during UUID set initialization", e))
                .subscribe();
    }
}
