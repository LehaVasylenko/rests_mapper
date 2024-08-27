package com.tabletki_mapper.mapper.model.nomenklatura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Schema
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierCode {
    @Schema(description = "Ідентифікатор постачальника", example = "Morion")
    @JsonProperty("ID")
    String id;

    @Schema(description = "Значення коду товару постачальника", example = "7167")
    @JsonProperty("Code")
    String code;
}
