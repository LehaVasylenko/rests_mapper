package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.dto.EventDTO;
import com.tabletki_mapper.mapper.dto.ShopPointDTO;
import com.tabletki_mapper.mapper.dto.UserDTO;
import com.tabletki_mapper.mapper.mapper.EventMapper;
import com.tabletki_mapper.mapper.mapper.ShopPointMapper;
import com.tabletki_mapper.mapper.mapper.UserMapper;
import com.tabletki_mapper.mapper.model.ShopPoint;
import com.tabletki_mapper.mapper.model.UserDB;
import com.tabletki_mapper.mapper.model.user.Role;
import com.tabletki_mapper.mapper.repository.EventRepository;
import com.tabletki_mapper.mapper.repository.ShopPointRepository;
import com.tabletki_mapper.mapper.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService {

    EventRepository repository;
    UserRepository userRepository;
    ShopPointRepository shopRepository;

    public Mono<List<EventDTO>> processLogs(Integer time) {
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(time);
        return repository.findAll()
                .filter(event -> event.getTime().isAfter(thresholdTime))
                .parallel() // Начало параллельной обработки
                .runOn(Schedulers.parallel()) // Определение пула потоков для параллельной обработки
                .map(EventMapper.INSTANCE::toDto)
                .sequential() // Возвращение к последовательному потоку
                .collectList();
    }

    public Mono<Integer> processUsers(List<UserDTO> users) {
        List<UserDB> userList = users.stream()
                .map(userDTO -> {
                    UserDB model = UserMapper.INSTANCE.toModel(userDTO);
                    model.setRole(String.valueOf(Role.SHOP));
                    return model;
                }).toList();

        return userRepository.saveAll(userList)
                .count() // Подсчет количества сохраненных пользователей
                .map(Long::intValue); // Преобразование Long в Integer
    }

    public Mono<Integer> processShops(List<ShopPointDTO> shopPointDTOS) {
        // Фильтруем записи по уникальности поля morionShopId
        Map<String, ShopPointDTO> uniqueShopPointsMap = shopPointDTOS.stream()
                .collect(Collectors.toMap(
                        ShopPointDTO::getMorionShopId, // Ключ — это morionShopId
                        Function.identity(),           // Значение — сам DTO
                        (existing, replacement) -> existing)); // Если есть дубликат, оставляем первый

        List<ShopPointDTO> uniqueShopPoints = new ArrayList<>(uniqueShopPointsMap.values());

        return Flux.fromIterable(uniqueShopPoints)
                .flatMap(dto -> shopRepository.findByMorionShopId(dto.getMorionShopId())
                        .flatMap(existingShop -> Mono.just(false)) // Магазин уже существует, не вставляем
                        .switchIfEmpty(Mono.defer(() -> {
                            ShopPoint shopPoint = ShopPointMapper.INSTANCE.toModel(dto);
                            return shopRepository.save(shopPoint).thenReturn(true); // Магазин вставлен
                        }))
                )
                .filter(isInserted -> isInserted) // Фильтруем только вставленные записи
                .count()
                .map(Long::intValue);
    }

}
