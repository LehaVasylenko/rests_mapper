package com.tabletki_mapper.mapper.model.nomenklatura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Об'єкт для передачі даних щодо номенклатури на ТТ")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nomenklatura {

    @Schema(hidden = true)
    String externalPharmacyId;

    @Schema(hidden = true)
    String username;

    @Schema(description = "Перелік позицій із цінами та залишками")
    @JsonProperty("Offers")
    List<Offers> offers;

    @Schema(description = "Перелік постачальників")
    @JsonProperty("Suppliers")
    List<Suppliers> suppliers;
}
