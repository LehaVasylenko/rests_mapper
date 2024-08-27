package com.tabletki_mapper.mapper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeoaptekaDataModel {
    String id;
    String name;
    String home;
    Float quant;
    Float price;
    @JsonProperty("price_cntr")
    Float priceCntr;
    Integer pfactor = 1;
}
