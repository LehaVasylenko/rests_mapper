package com.tabletki_mapper.mapper.event;

import com.tabletki_mapper.mapper.model.ShopPoint;
import com.tabletki_mapper.mapper.model.UserDB;
import com.tabletki_mapper.mapper.model.user.Role;
import com.tabletki_mapper.mapper.repository.ShopPointRepository;
import com.tabletki_mapper.mapper.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 07.08.2024
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultUserInitializer implements ApplicationListener<ContextRefreshedEvent> {

    UserRepository repository;
    ShopPointRepository shopPointRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        repository.findByUsername("admin")
                .switchIfEmpty(Mono.defer(() -> {
                    UserDB admin = UserDB.builder()
                            .username("admin")
                            .password("{noop}admin")
                            .role(Role.ADMIN.toString())
                            .morionKey("1")
                            .morionLogin("1")
                            .morionCorpId("1")
                            .build();
                    return repository.save(admin);
                }))
                .then(
                        repository.findByUsername("user")
                                .switchIfEmpty(Mono.defer(() -> {
                                    UserDB user = UserDB.builder()
                                            .username("user")
                                            .password("{noop}user")
                                            .role(Role.SHOP.toString())
                                            .morionKey("3564584e1adafe919c42a417458812baad082fd1")
                                            .morionLogin("farmaciya-odessa-1afb49a4")
                                            .morionCorpId("35092")
                                            .build();
                                    return repository.save(user);
                                }))
                )
                .then(
                        // Добавление дефолтных записей в таблицу shops
                        shopPointRepository.findByMorionShopId("1481397") // Проверяем наличие записи с id 1
                                .switchIfEmpty(Mono.defer(() -> {
                                    ShopPoint shop = ShopPoint.builder()
                                            .shopExtId("EXT123")
                                            .shopName("Default Shop")
                                            .shopHead("Default Head")
                                            .shopAddr("Default Address")
                                            .shopCode("12345")
                                            .morionShopId("1481397")
                                            .morionCorpId("35092")
                                            .build();
                                    return shopPointRepository.save(shop);
                                }))
                )
                .subscribe();
    }
}
