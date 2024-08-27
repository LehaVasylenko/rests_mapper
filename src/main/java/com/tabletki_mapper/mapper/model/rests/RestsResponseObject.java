package com.tabletki_mapper.mapper.model.rests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Об'єкт для передачі даних щодо результатів обробки запиту щодо залишків та цін")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestsResponseObject {

    @Schema(description = "Ознака наявності помилки приймання пакету з залишками аптек", example = "false")
    @JsonProperty("IsError")
    Boolean isError;

    @Schema(description = "Інформація про помилку", example = " ")
    @JsonProperty("ErrorMessage")
    String errorMessage;

    @Schema(description = "Масив результатів обробки даних аптек")
    @JsonProperty("BranchResults")
    List<BranchResult> branchResults;

}
