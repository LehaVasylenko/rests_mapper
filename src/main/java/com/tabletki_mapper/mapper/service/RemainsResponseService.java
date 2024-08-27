package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.model.remains.Remains;
import com.tabletki_mapper.mapper.model.remains.RemainsResponseObject;
import com.tabletki_mapper.mapper.model.remains.Store;
import com.tabletki_mapper.mapper.model.rests.BranchResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RemainsResponseService {

//    final ReactiveKafkaProducerTemplate<String, Remains> reactiveKafkaProducerTemplate;
    String TOPIC = "REMAINS";

    public Mono<RemainsResponseObject> processRemains(Remains remains, String username) {
        remains.setUsername(username);

        return Mono.fromCallable(() -> {
            List<BranchResult> storesResult = new ArrayList<>();
            int goodsCount = remains.getGoods() != null ? remains.getGoods().size() : 0;
            int storesCount = remains.getStores() != null ? remains.getStores().size() : 0;

            if (storesCount != 0) {
                for (Store store : remains.getStores()) {
                    storesResult.add(BranchResult.builder()
                            .branchID(store.getStoreCode())
                            .isError(false)
                            .errorMessage("")
                            .restCount(store.getRests().size())
                            .build());
                }
            }

            return RemainsResponseObject.builder()
                    .branchResults(storesResult)
                    .errorMessage("")
                    .goodsCount(goodsCount)
                    .storeCount(storesCount)
                    .isError(false)
                    .build();
        });//.flatMap(response -> {
//            return reactiveKafkaProducerTemplate.send(TOPIC, remains)
//                    .doOnSuccess(senderResult -> log.info("Sent to TOPIC {} by {} offset: {}", TOPIC, remains.getUsername(), senderResult.recordMetadata().offset()))
//                    .thenReturn(response);
//        });
    }
}
