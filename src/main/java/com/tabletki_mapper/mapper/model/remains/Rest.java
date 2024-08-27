package com.tabletki_mapper.mapper.model.remains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rest {
    String code;
    Float qty;
    Float price;
    @JsonProperty("PriceReserve")
    Float priceReserve;
}
