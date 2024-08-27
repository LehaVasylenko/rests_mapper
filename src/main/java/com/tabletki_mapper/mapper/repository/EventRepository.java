package com.tabletki_mapper.mapper.repository;

import com.tabletki_mapper.mapper.model.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, Long> {
}
