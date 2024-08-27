package com.tabletki_mapper.mapper.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tabletki_mapper.mapper.exception.NoAuthHeaderexception;
import com.tabletki_mapper.mapper.model.Event;
import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.nomenklatura.NomenklaturaResponseObject;
import com.tabletki_mapper.mapper.model.remains.Remains;
import com.tabletki_mapper.mapper.model.remains.RemainsResponseObject;
import com.tabletki_mapper.mapper.model.rests.Rests;
import com.tabletki_mapper.mapper.model.rests.RestsResponseObject;
import com.tabletki_mapper.mapper.repository.EventRepository;
import com.tabletki_mapper.mapper.service.NomenklaturaResponseService;
import com.tabletki_mapper.mapper.service.RemainsResponseService;
import com.tabletki_mapper.mapper.service.RestsResponseService;
import com.tabletki_mapper.mapper.validation.NoSpecialCharacters;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/Import")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "Номенклатура, залишки", description = "Перелік ресурсів для надсилання данних по номенклатурі та залишкам на ТТ")
public class Controller {
    EventRepository eventRepository;
    NomenklaturaResponseService nomenklaturaService;
    RestsResponseService restsService;
    RemainsResponseService remainsService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "Надіслати номенклатуру по ТТ",
            description = """
                    Робота з API здійснюється за протоколом HTTPS з обов'язковою авторизацією. Використовується Basic Authorization:\n
                    Запити повинні містити наступний Header: "Authorization": "Basic auth_str", де auth_str — Base64-закодована строка "login:password"\n
                    Формат вхідних даних, що підтримується: json\n
                    Для передачі даних  в запит необхідно додати Header  "Content-Type" із значенням: "application/json"\n
                    "branchID" -  ідентифікатор аптеки (серійний номер)\n
                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Інформація прийнята успішно",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NomenklaturaResponseObject.class))
                    })
            ,
            @ApiResponse(
                    responseCode = "400",
                    description = "Помилка в тілі запиту",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                "IsError": true,
                                                "ErrorMessage": "Unexpected character ('Ц' (code 1062 / 0x426)): was expecting a colon to separate field name and value",
                                                "OffersCount": 0,
                                                "IsSupplierError": true,
                                                "SupplierMessage": "Unexpected character ('Ц' (code 1062 / 0x426)): was expecting a colon to separate field name and value",
                                                "SuppliersCount": 0,
                                                "OffersWithSupplierCodesCount": 0
                                            }
                                            """))
                    }),
            @ApiResponse(
                    responseCode = "401",
                    description = "Помилка авторизації",
                    content = {
                            @Content(examples = @ExampleObject(value = "    "))
                    }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Помилка прав доступу",
                    content = {
                            @Content(examples = @ExampleObject(value = "Access Denied"))
                    }),
            @ApiResponse(
                    responseCode = "415",
                    description = "Помилка значення хедеру Content-Type",
                    content = {
                            @Content(examples = @ExampleObject(value = """
                                    {
                                        "IsError": true,
                                        "ErrorMessage": "415 UNSUPPORTED_MEDIA_TYPE \\"Content type 'application/xml' not supported for bodyType",
                                        "OffersCount": 0,
                                        "IsSupplierError": true,
                                        "SupplierMessage": "415 UNSUPPORTED_MEDIA_TYPE \\"Content type 'application/xml' not supported for bodyType",
                                        "SuppliersCount": 0,
                                        "OffersWithSupplierCodesCount": 0
                                    }
                                    """))
                    }),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка серверу",
                    content = {
                            @Content(examples = @ExampleObject(value = """
                                    {
                                        "IsError": true,
                                        "ErrorMessage": "Повідомлення про помилку",
                                        "OffersCount": 0,
                                        "IsSupplierError": true,
                                        "SupplierMessage": "Повідомлення про помилку",
                                        "SuppliersCount": 0,
                                        "OffersWithSupplierCodesCount": 0
                                    }
                                    """))
                    })
    })
    @PostMapping("/Ref/{branchId}")
    public Mono<NomenklaturaResponseObject> setNomenclature(@RequestBody Mono<Nomenklatura> nomenklaturaMono,
                                                            @PathVariable @NoSpecialCharacters @NotEmpty String branchId,
                                                            @AuthenticationPrincipal Mono<UserDetails> userDetailsMono,
                                                            ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        return getRequestBody(exchange, request)
                .flatMap(body -> {
                    Event event = getEvent(body, request);
                    Nomenklatura nomenklatura;
                    try {
                        nomenklatura = objectMapper.readValue(body, Nomenklatura.class);
                        exchange.getAttributes().put("cachedNomenklatura", nomenklatura);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize request body: {}", e.getMessage());
                        saveBadEvent(event, body, e.getMessage());
                        return Mono.error(e); // Возвращаем ошибку в случае неудачной десериализации
                    }

                    return eventRepository.save(event)
                            .then(userDetailsMono
                                    .flatMap(userDetails -> nomenklaturaService.processNomenklatura(nomenklatura, branchId, userDetails.getUsername()))
                                    .doOnError(error -> saveBadEvent(event, body, error.getMessage()))
                                    .doOnSuccess(response -> {
                                        try {
                                            event.setResponseBody(objectMapper.writeValueAsString(response));
                                        } catch (JsonProcessingException e) {
                                            event.setResponseBody(response.toString());
                                        }
                                        eventRepository.save(event).doOnError(error -> log.error("", error)).subscribe();
                                    })
                            );
                });
    }

    @Operation(summary = "Вивантаження даних про залишки та ціни аптек",
            description = """
                    Робота з API здійснюється за протоколом HTTPS з обов'язковою авторизацією. Використовується Basic Authorization:\n
                    Запити повинні містити наступний Header: "Authorization": "Basic auth_str", де auth_str — Base64-закодована строка "login:password"\n
                    Формат вхідних даних, що підтримується: json\n
                    Для передачі даних  в запит необхідно додати Header  "Content-Type" із значенням: "application/json"\n
                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Інформація прийнята успішно",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RestsResponseObject.class))
                    })
            ,
            @ApiResponse(
                    responseCode = "400",
                    description = "Помилка в тілі запиту",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                                 "IsError": true,
                                                 "ErrorMessage": "Unexpected character ('і' (code 1110 / 0x456)): was expecting double-quote to start field name",
                                                 "BranchResults": null
                                             }
                                            """))
                    }),
            @ApiResponse(
                    responseCode = "401",
                    description = "Помилка авторизації",
                    content = {
                            @Content(examples = @ExampleObject(value = "    "))
                    }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Помилка прав доступу",
                    content = {
                            @Content(examples = @ExampleObject(value = "Access Denied"))
                    }),
            @ApiResponse(
                    responseCode = "415",
                    description = "Помилка значення хедеру Content-Type",
                    content = {
                            @Content(examples = @ExampleObject(value = """
                                    {
                                         "IsError": true,
                                         "ErrorMessage": "415 UNSUPPORTED_MEDIA_TYPE \\"Content type 'application/xml' not supported for bodyType",
                                         "BranchResults": null
                                    }
                                    """))
                    }),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка серверу",
                    content = {
                            @Content(examples = @ExampleObject(value = """
                                    {
                                         "IsError": true,
                                         "ErrorMessage": "Повідомлення про помилку",
                                         "BranchResults": null
                                    }
                                    """))
                    })
    })
    @PostMapping("/Rests")
    public Mono<RestsResponseObject> setRests(@RequestBody Mono<Rests> restsMono,
                                              @AuthenticationPrincipal Mono<UserDetails> userDetailsMono,
                                              ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        return getRequestBody(exchange, request)
                .flatMap(body -> {
                    Event event = getEvent(body, request);
                    Rests rests;
                    try {
                        rests = objectMapper.readValue(body, Rests.class);
                        exchange.getAttributes().put("cachedNomenklatura", rests);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize request body: {}", e.getMessage());
                        saveBadEvent(event, body, e.getMessage());
                        return Mono.error(e); // Возвращаем ошибку в случае неудачной десериализации
                    }

                    return eventRepository.save(event)
                            .then(userDetailsMono
                                    .flatMap(userDetails -> restsService.processRests(rests, userDetails.getUsername()))
                                    .doOnError(error -> saveBadEvent(event, body, error.getMessage()))
                                    .doOnSuccess(response -> {
                                        try {
                                            event.setResponseBody(objectMapper.writeValueAsString(response));
                                        } catch (JsonProcessingException e) {
                                            event.setResponseBody(response.toString());
                                        }
                                        eventRepository.save(event).doOnError(error -> log.error("", error)).subscribe();
                                    })
                            );
                });
    }

    @PostMapping("/Rests-no")
    public Mono<RestsResponseObject> setRestsNoSerialization(@RequestBody Rests rests,
                                              @AuthenticationPrincipal Mono<UserDetails> userDetailsMono) {
        // Получаем имя пользователя из Mono<UserDetails>
        return userDetailsMono
                .map(UserDetails::getUsername) // Предположим, что метод getUsername() возвращает имя пользователя
                .flatMap(username -> {
                    // Передаем данные в сервис для обработки
                    return restsService.processRests(rests, username);
                })
                .onErrorResume(e -> {
                    // Обработка ошибок
                    log.error("Error processing rests", e);
                    RestsResponseObject errorResponse = RestsResponseObject.builder()
                            .branchResults(new ArrayList<>())
                            .errorMessage("An error occurred while processing the request: " + e.getMessage())
                            .isError(true)
                            .build();
                    return Mono.just(errorResponse);
                });
    }



    @Hidden
    @PostMapping("/Remains")
    public Mono<RemainsResponseObject> setRemains(@RequestBody Mono<Remains> remainsMono,
                                              @AuthenticationPrincipal Mono<UserDetails> userDetailsMono,
                                              ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        return getRequestBody(exchange, request)
                .flatMap(body -> {
                    Event event = getEvent(body, request);
                    Remains remains;
                    try {
                        remains = objectMapper.readValue(body, Remains.class);
                        // Сохранение десериализованного объекта в атрибутах
                        exchange.getAttributes().put("cachedNomenklatura", remains);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize request body: {}", e.getMessage());
                        saveBadEvent(event, body, e.getMessage());
                        return Mono.error(e); // Возвращаем ошибку в случае неудачной десериализации
                    }

                    return eventRepository.save(event)
                            .then(userDetailsMono
                                    .flatMap(userDetails -> remainsService.processRemains(remains, userDetails.getUsername()))
                                    .doOnError(error -> saveBadEvent(event, body, error.getMessage()))
                                    .doOnSuccess(response -> {
                                        try {
                                            event.setResponseBody(objectMapper.writeValueAsString(response));
                                        } catch (JsonProcessingException e) {
                                            event.setResponseBody(response.toString());
                                        }
                                        eventRepository.save(event).doOnError(error -> log.error("", error)).subscribe();
                                    })
                            );
                });
    }

    private @NotNull Event getEvent(String body, ServerHttpRequest request) {
        Event event = Event.builder()
                .time(LocalDateTime.now())
                .path(request.getPath().value())
                .headers(request.getHeaders().toString())
                .requestBody("valid")
                .responseBody("")  // Пусто, пока нет ответа
                .isError(false)
                .build();
        String[] creds;
        try {
            creds = logHeaders(request);
        } catch (NoAuthHeaderexception e) {
            saveEventOnBadAuth(body, e, event);
            throw new NoAuthHeaderexception(e);
        }

        event.setUsername(creds[0]);
        event.setPassword(creds[1]);
        return event;
    }

    private void saveEventOnBadAuth(String body, NoAuthHeaderexception e, Event event) {
        event.setErrorMessage(e.getMessage());
        event.setIsError(true);
        event.setRequestBody(body.replaceAll("\n","").replaceAll(" ", ""));
        eventRepository.save(event).doOnError(error -> log.error("", error)).subscribe();
    }

    private static @NotNull Mono<String> getRequestBody(ServerWebExchange exchange, ServerHttpRequest request) {
        return DataBufferUtils.join(request.getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .doOnNext(body -> {
                    // Кешируем тело запроса в атрибутах для дальнейшего использования
                    exchange.getAttributes().put("cachedBody", body);
                });
    }

    private void saveBadEvent(Event event, String body, String e) {
        event.setIsError(true);
        event.setRequestBody(body.replaceAll("\n", "").replaceAll(" ", ""));
        event.setErrorMessage(e);
        eventRepository.save(event).doOnError(error -> log.error("", error)).subscribe();
    }

    //logs
    private String[] logHeaders(ServerHttpRequest request) {
        String[] result = getCredentials(request
                .getHeaders()
                .getFirst("Authorization"));

        log.info("{} -> {}", result[0], result[1]);
        log.info(request.getHeaders().toString());
//        request.getHeaders().forEach((key, value) -> {
//            log.info("[{}] : {}", key, value);
//        });
        return result;
    }

    private String[] getCredentials(String authHeader) {
        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            return decodedString.split(":", 2);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            log.warn("Failed to decode credentials", e);
            return new String[]{"", ""};
        } catch (NullPointerException ex) {
            throw new NoAuthHeaderexception("Unauthorized");
        }
    }
}
