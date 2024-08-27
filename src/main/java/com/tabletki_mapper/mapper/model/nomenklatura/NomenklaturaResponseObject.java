package com.tabletki_mapper.mapper.model.nomenklatura;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Schema(description = "Об'єкт для передачі результатів обробки даних щодо номенклатури на ТТ")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NomenklaturaResponseObject {
    @Schema(description = "Ознака наявності помилки приймання даних", example = "false")
    @JsonProperty("IsError")
    boolean isError;

    @Schema(description = "Інформація про помилку", example = " ")
    @JsonProperty("ErrorMessage")
    String errorMessage;

    @Schema(description = "Кількість прийнятих для обробки товарів (цілочислене значення)", example = "100500")
    @JsonProperty("OffersCount")
    Integer offersCount;

    @Schema(description = "Ознака наявності помилки даних про постачальників та/або коди постачальників", example = "false")
    @JsonProperty("IsSupplierError")
    boolean isSupplierError;

    @Schema(description = "Інформація про помилку/попередження під час обробки даних про постачальників та/або коди постачальників", example = " ")
    @JsonProperty("SupplierMessage")
    String supplierMessage;

    @Schema(description = "Кількість прийнятих для обробки постачальників кодів (цілочислене значення)", example = "3")
    @JsonProperty("SuppliersCount")
    Integer suppliersCount;

    @Schema(description = "Кількість товарів з кодами постачальників, прийнятих для обробки (цілочислене значення)", example = "100500")
    @JsonProperty("OffersWithSupplierCodesCount")
    Integer offersWithSupplierCodesCount;
}
