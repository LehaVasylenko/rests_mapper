package com.tabletki_mapper.mapper.repository;

import com.tabletki_mapper.mapper.model.DrugEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 04.08.2024
 */
@Repository
public interface DrugEntityRepository extends ReactiveCrudRepository<DrugEntity, UUID> {
    Flux<DrugEntity> findByUserLogin(String userLogin);
    Mono<DrugEntity> findByDrugIdAndUserLogin(String drugId, String userLogin);

    @Query("SELECT id FROM drugs")
    Flux<UUID> findAllDrugIds();
}
