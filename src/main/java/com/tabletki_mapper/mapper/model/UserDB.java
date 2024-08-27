package com.tabletki_mapper.mapper.model;

import com.tabletki_mapper.mapper.model.user.Role;
import com.tabletki_mapper.mapper.model.user.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("users")
public class UserDB implements Serializable {
    @Id
    @Column("user_id")
    Long userId;
    String username;
    String password;
    String role;

    @Column("morion_login")
    String morionLogin;

    @Column("morion_key")
    String morionKey;

    @Column("morion_corp_id")
    String morionCorpId;
}
