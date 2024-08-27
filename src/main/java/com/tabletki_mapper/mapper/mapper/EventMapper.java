package com.tabletki_mapper.mapper.mapper;

import com.tabletki_mapper.mapper.dto.EventDTO;
import com.tabletki_mapper.mapper.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Mapper
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(source = "time", target = "time")
    @Mapping(source = "path", target = "path")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "headers", target = "headers")
    @Mapping(source = "length", target = "length")
    @Mapping(source = "requestBody", target = "requestBody")
    @Mapping(source = "responseBody", target = "responseBody")
    @Mapping(source = "isError", target = "isError")
    @Mapping(source = "errorMessage", target = "errorMessage")
    Event toModel(EventDTO eventDTO);

    @Mapping(source = "time", target = "time")
    @Mapping(source = "path", target = "path")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "headers", target = "headers")
    @Mapping(source = "length", target = "length")
    @Mapping(source = "requestBody", target = "requestBody")
    @Mapping(source = "responseBody", target = "responseBody")
    @Mapping(source = "isError", target = "isError")
    @Mapping(source = "errorMessage", target = "errorMessage")
    EventDTO toDto(Event event);
}
