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
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * reactive
 * Author: Vasylenko Oleksii
 * Date: 17.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkyNetService {

    final WebClient webClient;
    int counter = 0;

    public Mono<Void> send(List<SkyNetData> dataList) {
        String url = "https://skynet.morion.ua/stream/put-data";
        log.info("Hello SKYNET! -> #{} prices", dataList.size());
        return Flux.fromIterable(dataList)
                .flatMap(data -> webClient
                        .post()
                        .uri(url)
                        .headers(httpHeaders -> httpHeaders.addAll(data.getHeaders())) // Добавляем заголовки из SkyNetData
                        .bodyValue(data.getBody()) // Передаем тело запроса
                        .retrieve()
                        .onStatus(HttpStatusCode::is2xxSuccessful, response -> {
                            log.info("Request #{}: {}: {}", counter++, response.statusCode(), response.headers().header("X-Request-Id"));
                            return Mono.empty();  // Продолжаем обработку при успехе
                        })
                        .onStatus(HttpStatusCode::is4xxClientError, response -> {
                            log.error("Client error: {}: {}", response.statusCode(), response.headers().header("X-Request-Id"));
                            return response.bodyToMono(String.class)
                                    .doOnNext(body400 -> log.error("Request #{}: {}", counter++, body400))
                                    .then(Mono.empty());  // Логируем ошибку и продолжаем обработку
                        })
                        .onStatus(HttpStatusCode::is5xxServerError, response -> {
                            log.error("Server error: {}: {}", response.statusCode(), response.headers().header("X-Request-Id"));
                            return response.bodyToMono(String.class)
                                    .doOnNext(body500 -> log.error("Request #{}: {}", counter++, body500))
                                    .then(Mono.empty());  // Логируем ошибку и продолжаем обработку
                        })
                        .bodyToMono(String.class)  // Захватываем и логируем тело ответа
                        .doOnNext(responseBody -> log.info("Response received: {}", responseBody))
                        .then()
                )
                .doOnError(error -> log.error("Error occurred during sending request: {}", error.getMessage()))
                .then();  // Возвращаем Mono<Void>, указывая на завершение
    }

}


