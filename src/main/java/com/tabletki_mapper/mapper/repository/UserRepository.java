package com.tabletki_mapper.mapper.repository;

import com.tabletki_mapper.mapper.model.UserDB;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserDB, Long> {
    Mono<UserDB> findByUsername(String username);
}
