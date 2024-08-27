package com.tabletki_mapper.mapper.model.rests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Результат обробки даних кожної ТТ")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchResult {
    @Schema(description = "Внутрішній код ТТ в обліковій системі", example = "TT12345")
    @JsonProperty("BranchID")
    String branchID;

    @Schema(description = "Ознака наявності помилки приймання залишків аптеки", example = "false")
    @JsonProperty("IsError")
    Boolean isError;

    @Schema(description = "Інформація про помилку", example = " ")
    @JsonProperty("ErrorMessage")
    String errorMessage;

    @Schema(description = "Кількість прийнятих товарів з залишками", example = "100500")
    @JsonProperty("RestCount")
    Integer restCount;
}
