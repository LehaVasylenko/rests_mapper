package com.tabletki_mapper.mapper.model.nomenklatura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Suppliers {
    @Schema(description = "Ідентифікатор постачальника", example = "Morion")
    @JsonProperty("ID")
    String id;

    @Schema(description = "Найменування постачальника", example = "Morion")
    @JsonProperty("Name")
    String name;

    @Schema(description = "ЄДРПОУ постачальника", example = "123456789")
    @JsonProperty("Edrpo")
    String edrpo;
}