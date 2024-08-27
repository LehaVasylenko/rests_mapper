package com.tabletki_mapper.mapper.mapper;

import com.tabletki_mapper.mapper.dto.UserDTO;
import com.tabletki_mapper.mapper.model.UserDB;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "morionLogin", target = "morionLogin")
    @Mapping(source = "morionKey", target = "morionKey")
    @Mapping(source = "morionCorpId", target = "morionCorpId")
    UserDB toModel(UserDTO userDto);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "morionLogin", target = "morionLogin")
    @Mapping(source = "morionKey", target = "morionKey")
    @Mapping(source = "morionCorpId", target = "morionCorpId")
    UserDTO toDto(UserDB userDb);
}
