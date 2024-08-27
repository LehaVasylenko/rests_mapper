package com.tabletki_mapper.mapper.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.net.http.HttpHeaders;
import java.util.List;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserData {
    UserDB user;
    String shopExtId;
    List<GeoaptekaDataModel> payload;
}
