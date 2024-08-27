package com.tabletki_mapper.mapper.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 07.08.2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetaDataSkyNet {
    String id;
    String name;
    String head;
    String addr;
    String code;
    String htag;
}
