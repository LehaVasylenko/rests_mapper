package com.tabletki_mapper.mapper.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DrugEntityDTO {
    UUID id;
    String userLogin;
    String drugId;
    String drugName;
    String drugProducer;
    String morionId;
    String optimaId;
    String barcode;
    String home;
    Integer pfactor;

    public String getCacheKey() {
        return userLogin + ":" + drugId;
    }

    public String getMorionDescription() {
        return this.drugName + " " + this.drugProducer;
    }
}
