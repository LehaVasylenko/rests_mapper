package com.tabletki_mapper.mapper.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopPointDTO {
    String shopExtId;
    String shopName;
    String shopHead;
    String shopAddr;
    String shopCode;
    String morionCorpId;
    String morionShopId;
}
