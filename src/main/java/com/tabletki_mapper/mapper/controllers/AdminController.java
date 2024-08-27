package com.tabletki_mapper.mapper.controllers;

import com.tabletki_mapper.mapper.dto.EventDTO;
import com.tabletki_mapper.mapper.dto.ShopPointDTO;
import com.tabletki_mapper.mapper.dto.UserDTO;
import com.tabletki_mapper.mapper.service.AdminService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    AdminService service;

    @GetMapping("/get-zlog/{time}")
    public Mono<List<EventDTO>> getlogs(@PathVariable Integer time) {
        return service.processLogs(time);
    }

    @PostMapping("/add/user")
    public Mono<ResponseEntity<Integer>> addUser(@RequestBody Mono<List<UserDTO>> newUsers) {
        return newUsers
                .flatMap(service::processUsers)
                .map(count -> ResponseEntity.status(201).body(count))
                .onErrorResume(AdminController::applyError);
    }

    @PostMapping("/add/shop")
    public Mono<ResponseEntity<Integer>> addShop(@RequestBody Mono<List<ShopPointDTO>> newShops) {
        return newShops
                .flatMap(service::processShops)
                .map(count -> ResponseEntity.status(201).body(count))
                .onErrorResume(AdminController::applyError);
    }

    private static Mono<? extends ResponseEntity<Integer>> applyError(Throwable e) {
        log.error("Error occurred while adding data: {}", e.getMessage());
        return Mono.just(ResponseEntity.status(400).build());
    }
}
