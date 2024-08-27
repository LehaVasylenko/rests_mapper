package com.tabletki_mapper.mapper.model.rests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Інформація щодо залишків по одній ТТ")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Branch {

    @Schema(description = "Внутрішній код ТТ", example = "TT12345")
    @JsonProperty("Code")
    String code;

    @Schema(description = "Перелік позицій, цін та кількості препаратів, доступних в ТТ")
    @JsonProperty("Rests")
    List<Rest> rests;

    @Schema(description = "Дата/час актуальності залишків", example = "12.03.2024 12:30:34")
    @JsonProperty("DateTime")
    String dateTime;
}
