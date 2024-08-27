package com.tabletki_mapper.mapper.repository;

import com.tabletki_mapper.mapper.model.ShopPoint;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Repository
public interface ShopPointRepository extends ReactiveCrudRepository<ShopPoint, Long> {
    Mono<ShopPoint> findByMorionShopId(String morionShopId);
    Mono<ShopPoint> findByShopExtIdAndMorionCorpId(String shopExtId, String morionCorpId);
}
