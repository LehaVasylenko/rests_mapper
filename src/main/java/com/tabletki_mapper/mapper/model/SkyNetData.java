package com.tabletki_mapper.mapper.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;


/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 20.08.2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkyNetData {
    HttpHeaders headers;
    byte[] body;
}
