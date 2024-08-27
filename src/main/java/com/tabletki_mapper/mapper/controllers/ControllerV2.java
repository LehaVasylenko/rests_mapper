package com.tabletki_mapper.mapper.controllers;

import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.nomenklatura.NomenklaturaResponseObject;
import com.tabletki_mapper.mapper.model.remains.RemainsResponseObject;
import com.tabletki_mapper.mapper.model.rests.Rests;
import com.tabletki_mapper.mapper.model.rests.RestsResponseObject;
import com.tabletki_mapper.mapper.service.NomenklaturaResponseService;
import com.tabletki_mapper.mapper.service.RestsResponseService;
import com.tabletki_mapper.mapper.validation.NoSpecialCharacters;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 21.08.2024
 */
@Slf4j
@RestController
@RequestMapping("/v2/Import")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ControllerV2 {

    NomenklaturaResponseService nomenklaturaService;
    RestsResponseService restsService;

    @PostMapping("/Ref/{branchId}")
    public Mono<NomenklaturaResponseObject> setNomenclature(@RequestBody Nomenklatura nomenklaturaMono,
                                                            @PathVariable @NoSpecialCharacters @NotEmpty String branchId,
                                                            @AuthenticationPrincipal UserDetails userDetailsMono,
                                                            ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        log.info(headers.toString());
        log.info(exchange.getRequest().getURI().getPath());
        return nomenklaturaService.processNomenklatura(nomenklaturaMono, branchId, userDetailsMono.getUsername());
    }

    @PostMapping("/Rests")
    public Mono<RestsResponseObject> setRests(@RequestBody Rests restsMono,
                                              @AuthenticationPrincipal UserDetails userDetailsMono,
                                              ServerWebExchange exchange) {
        return restsService.processRests(restsMono, userDetailsMono.getUsername());
    }
}
