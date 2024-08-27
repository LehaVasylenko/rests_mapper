package com.tabletki_mapper.mapper.mapper;

import com.tabletki_mapper.mapper.dto.MetaDataSkyNet;
import com.tabletki_mapper.mapper.model.ShopPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 07.08.2024
 */
@Mapper
public interface ShopPointToMetadata {
    ShopPointToMetadata INSTANSE = Mappers.getMapper(ShopPointToMetadata.class);

    @Mapping(source = "shopExtId", target = "id")
    @Mapping(source = "shopName", target = "name")
    @Mapping(source = "shopHead", target = "head")
    @Mapping(source = "shopAddr", target = "addr")
    @Mapping(source = "shopCode", target = "code")
    MetaDataSkyNet toMetadate(ShopPoint point);
}
