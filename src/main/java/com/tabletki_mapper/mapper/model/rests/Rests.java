package com.tabletki_mapper.mapper.model.rests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Об'єкт для передачі даних щодо залишків на ТТ")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rests {
    @Schema(hidden = true)
    private String username;

    @Schema(description = "Перелік ТТ")
    @JsonProperty("Branches")
    private List<Branch> branches;

}
