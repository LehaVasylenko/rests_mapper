package com.tabletki_mapper.mapper.model.nomenklatura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Schema
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"Code", "Name", "Producer", "VAT"})
public class Offers {
    @Schema(description = "Код препарата у внутрішній обліковій системі", example = "Code-1234")
    @JsonProperty("Code")
    String code;

    @Schema(description = "Назва препарату", example = "Цитрамон-Дарниця №6")
    @JsonProperty("Name")
    String name;

    @Schema(description = "Виробник препарату", example = "Дарниця")
    @JsonProperty("Producer")
    String producer;

    @Schema(description = "ПДВ (опціонально)", example = "10.0")
    @JsonProperty("VAT")
    Integer vat;

    String home;
    Integer pfactor;

    @Schema(description = "Перелік кодів постачальників")
    @JsonProperty("SupplierCodes")
    List<SupplierCode> supplierCodes;
}