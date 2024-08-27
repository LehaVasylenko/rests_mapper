package com.tabletki_mapper.mapper.controllers;

import com.tabletki_mapper.mapper.dto.HeaderRequestDTO;
import com.tabletki_mapper.mapper.model.rests.RestsResponseObject;
import com.tabletki_mapper.mapper.service.HelpHeavyUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 07.08.2024
 */
@RestController
@RequestMapping("/help")
@RequiredArgsConstructor
@Tag(name = "Допомога користувачеві")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HelpController {

    HelpHeavyUserService service;

    @Operation(summary = "Отримання правильного значення хедеру 'Authorization' для правильної аутентифікації",
            description = """
                    Ендпоінт для самоперевірки. Повертає значення у форматі 'Basic ' + Base64-encoded строки у форматі 'login:password'
                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Інформація прийнята успішно",
                    content = {
                            @Content(examples = @ExampleObject(value = "Basic dXNlcjpwYXNzd29yZA=="))
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Помилка в тілі запиту"),
            @ApiResponse(
                    responseCode = "415",
                    description = "Помилка хедеру Content-Type"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Помилка серверу")
    })
    @PostMapping("/get/auth-header")
    public Mono<String> getHeader(@RequestBody Mono<HeaderRequestDTO> headerRequestDTOMono) {
        return headerRequestDTOMono.flatMap(service::getHeader);
    }

}
