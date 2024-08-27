package com.tabletki_mapper.mapper.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * mapper-executor
 * Author: Vasylenko Oleksii
 * Date: 04.08.2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("drugs")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Указываем, что включаем только явно указанные поля
public class DrugEntity {

    @Id
    UUID id;

    @Column("user_login")
    @EqualsAndHashCode.Include
    String userLogin;

    @Column("drug_id")
    @EqualsAndHashCode.Include
    String drugId;
    
    @Column("drug_name")
    @EqualsAndHashCode.Include
    String drugName;

    @Column("drug_producer")
    @EqualsAndHashCode.Include
    String drugProducer;

    @Column("morion_id")
    String morionId;

    @Column("optima_id")
    String optimaId;

    @Column("barcode")
    String barcode;

    @Column("home")
    String home;

    @Column("pfactor")
    Integer pfactor;
}
