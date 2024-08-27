package com.tabletki_mapper.mapper.mapper;

import com.tabletki_mapper.mapper.dto.EventDTO;
import com.tabletki_mapper.mapper.dto.ShopPointDTO;
import com.tabletki_mapper.mapper.model.Event;
import com.tabletki_mapper.mapper.model.ShopPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Mapper
public interface ShopPointMapper {
    ShopPointMapper INSTANCE = Mappers.getMapper(ShopPointMapper.class);

    @Mapping(source = "shopExtId", target = "shopExtId")
    @Mapping(source = "shopName", target = "shopName")
    @Mapping(source = "shopHead", target = "shopHead")
    @Mapping(source = "shopAddr", target = "shopAddr")
    @Mapping(source = "shopCode", target = "shopCode")
    @Mapping(source = "morionShopId", target = "morionShopId")
    @Mapping(source = "morionCorpId", target = "morionCorpId")
    ShopPoint toModel(ShopPointDTO dto);

    @Mapping(source = "shopExtId", target = "shopExtId")
    @Mapping(source = "shopName", target = "shopName")
    @Mapping(source = "shopHead", target = "shopHead")
    @Mapping(source = "shopAddr", target = "shopAddr")
    @Mapping(source = "shopCode", target = "shopCode")
    @Mapping(source = "morionShopId", target = "morionShopId")
    @Mapping(source = "morionCorpId", target = "morionCorpId")
    ShopPointDTO toDto(ShopPoint model);
}
