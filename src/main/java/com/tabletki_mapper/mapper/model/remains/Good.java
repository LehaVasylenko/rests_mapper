package com.tabletki_mapper.mapper.model.remains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 05.08.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Good {
    String code;
    String name;
    String producer;
    Double vat;
    String code1;
    String code2;
    String code7;
    String barcode;
    String home;
    Integer pfactor;

    public String getMorionDescription() {
        return new StringBuilder()
                .append(name)
                .append(" ")
                .append(producer)
                .append("[Morion id: ")
                .append(code1)
                .append("][Optima id: ")
                .append(code2)
                .append("][BADM id: ")
                .append(code7)
                .append("][Barcode: ")
                .append(barcode)
                .append("]")
                .toString();
    }
}
