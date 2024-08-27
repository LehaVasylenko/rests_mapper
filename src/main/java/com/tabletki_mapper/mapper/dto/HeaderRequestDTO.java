package com.tabletki_mapper.mapper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 07.08.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(description = "Об'єкт для запиту для отримання хедеру авторизації")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HeaderRequestDTO {
    @Schema(description = "Логін", example = "user")
    String login;

    @Schema(description = "Пароль", example = "password")
    String password;
}
