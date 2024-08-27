package com.tabletki_mapper.mapper.mapper;

import com.tabletki_mapper.mapper.dto.DrugEntityDTO;
import com.tabletki_mapper.mapper.model.DrugEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Mapper
public interface DrugEntityMapper {

    DrugEntityMapper INSTANCE = Mappers.getMapper(DrugEntityMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userLogin", target = "userLogin")
    @Mapping(source = "drugId", target = "drugId")
    @Mapping(source = "drugName", target = "drugName")
    @Mapping(source = "drugProducer", target = "drugProducer")
    @Mapping(source = "morionId", target = "morionId")
    @Mapping(source = "optimaId", target = "optimaId")
    @Mapping(source = "barcode", target = "barcode")
    @Mapping(source = "home", target = "home")
    @Mapping(source = "pfactor", target = "pfactor")
    DrugEntity toModel(DrugEntityDTO drugEntityDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userLogin", target = "userLogin")
    @Mapping(source = "drugId", target = "drugId")
    @Mapping(source = "drugName", target = "drugName")
    @Mapping(source = "drugProducer", target = "drugProducer")
    @Mapping(source = "morionId", target = "morionId")
    @Mapping(source = "optimaId", target = "optimaId")
    @Mapping(source = "barcode", target = "barcode")
    @Mapping(source = "home", target = "home")
    @Mapping(source = "pfactor", target = "pfactor")
    DrugEntityDTO toDto(DrugEntity drugEntity);
}
