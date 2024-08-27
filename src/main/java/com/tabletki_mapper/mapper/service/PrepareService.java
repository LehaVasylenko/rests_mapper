package com.tabletki_mapper.mapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tabletki_mapper.mapper.dto.MetaDataSkyNet;
import com.tabletki_mapper.mapper.mapper.ShopPointToMetadata;
import com.tabletki_mapper.mapper.model.GeoaptekaDataModel;
import com.tabletki_mapper.mapper.model.ShopPoint;
import com.tabletki_mapper.mapper.model.SkyNetData;
import com.tabletki_mapper.mapper.model.UserData;
import com.tabletki_mapper.mapper.repository.ShopPointRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 20.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrepareService {

    final ShopPointRepository shopPointRepository;
    final ReactiveRedisTemplate<String, ShopPoint> redisTemplate;
    final ObjectMapper objectMapper = new ObjectMapper();

    final SkyNetService service;

    @Value("${redis.prefix.shop}")
    String shopPrefix;

    int bodies = 0;
    int headers = 0;

    public Mono<Void> prepateData(List<UserData> dataList) {
        log.warn("Received {} reords", dataList.size());
        return Flux.fromIterable(dataList)
                .flatMap(data ->
                        getHttpHeaders(data.getShopExtId(), data.getUser().getMorionKey(), data.getUser().getMorionCorpId())
                                .flatMap(headers ->
                                        getBody(data.getPayload())
                                                .map(body -> SkyNetData.builder()
                                                        .headers(headers)
                                                        .body(body)
                                                        .build())
                                )
                )
                .collectList()  // Собираем все SkyNetData в список
                .flatMap(service::send)  // Отправляем список в сервис
                .doOnError(error -> log.error("Error occurred during preparing data for request: {}", error.getMessage()))
                .then();  // Завершаем Mono<Void>
    }

    private Mono<byte[]> getBody(List<GeoaptekaDataModel> model) {
        return Mono.fromCallable(() -> {
            try {

                String jsonInput = objectMapper.writeValueAsString(model);
                ByteArrayOutputStream obj = new ByteArrayOutputStream();
                try (GZIPOutputStream gzip = new GZIPOutputStream(obj)) {
                    gzip.write(jsonInput.getBytes(StandardCharsets.UTF_8));
                    gzip.flush();
                }
                //log.warn("Body prepared #{}", this.bodies++);
                return obj.toByteArray();
            } catch (IOException e) {
                log.error("Can't prepare body: {}", e.getMessage());
                return new byte[0];
            }
        });
    }

    private Mono<String> getMetadata(String shopExtId, String morionId) {
        String htag = "geoapt.ua";
        String cacheKey = shopPrefix + shopExtId + ":" + morionId;

        // Попытка получить ShopPoint из кэша
        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(shopPoint -> {
                    // Если ShopPoint есть в кэше, формируем метаданные
                    MetaDataSkyNet data = ShopPointToMetadata.INSTANSE.toMetadate(shopPoint);
                    data.setHtag(htag);
                    try {
                        String jsonString = objectMapper.writeValueAsString(data);
                        //log.warn("Metadata prepared #{}", this.headers++);
                        return Mono.just(Base64.getEncoder().encodeToString(jsonString.getBytes()));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize object to JSON: {}", e.getMessage());
                        return Mono.just("");
                    }
                })
                .switchIfEmpty(
                        // Если нет в кэше, запрашиваем из базы данных
                        shopPointRepository.findByShopExtIdAndMorionCorpId(shopExtId, morionId)
                                .flatMap(shopPoint -> {
                                    MetaDataSkyNet data = ShopPointToMetadata.INSTANSE.toMetadate(shopPoint);
                                    data.setHtag(htag);
                                    try {
                                        String jsonString = objectMapper.writeValueAsString(data);
                                        //log.warn("Metadata prepared from DB #{}", this.headers++);
                                        return redisTemplate.opsForValue().set(cacheKey, shopPoint)
                                                .then(Mono.just(Base64.getEncoder().encodeToString(jsonString.getBytes())));
                                    } catch (JsonProcessingException e) {
                                        log.error("Failed to serialize object to JSON: {}", e.getMessage());
                                        return Mono.just("");
                                    }
                                })
                                .onErrorReturn("")
                );
    }

    private Mono<HttpHeaders> getHttpHeaders(String shopExtId, String apiKey, String morionId) {
        return getMetadata(shopExtId, morionId)
                .map(metadata -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Encoding", "gzip");
                    headers.add("Content-Type", "application/json; charset=utf-8");
                    headers.add("Content-Meta", metadata);
                    headers.setBasicAuth("api", "key-" + apiKey);
                    return headers;
                });
    }
}
