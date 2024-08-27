package com.tabletki_mapper.mapper.model.rests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Інформація щодо залишків")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rest {
    @Schema(description = "Внутрішній код препарату", example = "Code-1234")
    @JsonProperty("Code")
    String code;

    @Schema(description = "Ціна препарату в ТТ", example = "123.55")
    @JsonProperty("Price")
    Float price;

    @Schema(description = "Доступна кількість препарату в ТТ", example = "3.5")
    @JsonProperty("Qty")
    Float qty;

    @Schema(description = "Ціна товару для бронювання, повинна бути або меншою за Price або дорівнювати Price", example = "119.99")
    @JsonProperty("PriceReserve")
    Float priceReserve;
}
