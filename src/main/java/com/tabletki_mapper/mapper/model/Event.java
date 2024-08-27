package com.tabletki_mapper.mapper.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("controller_events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    Long id;

    LocalDateTime time;
    String path;

    @Column("user_name")
    String username;

    String password;
    String headers;
    Integer length;

    @Column("request_body")
    String requestBody;

    @Column("response_body")
    String responseBody;

    @Column("is_error")
    Boolean isError;

    @Column("error_message")
    String errorMessage;
}
