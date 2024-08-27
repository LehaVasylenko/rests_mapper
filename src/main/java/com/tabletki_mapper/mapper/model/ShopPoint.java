package com.tabletki_mapper.mapper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("shops")
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopPoint {
    @Id
    Long id;

    @Column("shop_ext_id")
    String shopExtId;

    @Column("shop_name")
    String shopName;

    @Column("shop_head")
    String shopHead;

    @Column("shop_addr")
    String shopAddr;

    @Column("shop_code")
    String shopCode;

    @Column("shop_morion_shop_id")
    String morionShopId;

    @Column("shop_morion_corp_id")
    String morionCorpId;

    public String getCacheId() {
        return this.shopExtId + ":" + this.morionCorpId;
    }
}
