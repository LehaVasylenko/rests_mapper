package com.tabletki_mapper.mapper.model.remains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tabletki_mapper.mapper.model.rests.BranchResult;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemainsResponseObject {
    @JsonProperty("IsError")
    Boolean isError;

    @JsonProperty("ErrorMessage")
    String errorMessage;

    @JsonProperty("GoodsCount")
    Integer goodsCount;

    @JsonProperty("StoresCount")
    Integer storeCount;

    @JsonProperty("BranchResults")
    List<BranchResult> branchResults;
}
