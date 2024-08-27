package com.tabletki_mapper.mapper.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
public class EventDTO {
    LocalDateTime time;
    String path;
    String username;
    String password;
    String headers;
    Integer length;
    String requestBody;
    String responseBody;
    Boolean isError;
    String errorMessage;
}
