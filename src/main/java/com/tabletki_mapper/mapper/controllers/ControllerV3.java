package com.tabletki_mapper.mapper.controllers;

import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.nomenklatura.NomenklaturaResponseObject;
import com.tabletki_mapper.mapper.model.rests.Rests;
import com.tabletki_mapper.mapper.model.rests.RestsResponseObject;
import com.tabletki_mapper.mapper.service.NomenklaturaResponseService;
import com.tabletki_mapper.mapper.service.ProxyService;
import com.tabletki_mapper.mapper.service.RestsResponseService;
import com.tabletki_mapper.mapper.validation.NoSpecialCharacters;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 21.08.2024
 */
@Slf4j
@RestController
@RequestMapping("/v3/Import")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ControllerV3 {

    ProxyService service;

    @PostMapping("/Ref/{branchId}")
    public Mono<NomenklaturaResponseObject> setNomenclature(@RequestBody String nomenklatura,
                                                            @PathVariable @NoSpecialCharacters @NotEmpty String branchId,
                                                            @AuthenticationPrincipal UserDetails userDetails,
                                                            ServerWebExchange exchange) {
        return service.logAndProcessNomenklatura(nomenklatura, branchId, userDetails, exchange);
    }

    @PostMapping("/Rests")
    public Mono<RestsResponseObject> setRests(@RequestBody String rests,
                                              @AuthenticationPrincipal UserDetails userDetails,
                                              ServerWebExchange exchange) {
        return service.loadAndProcessRests(rests, userDetails, exchange);
    }
}
