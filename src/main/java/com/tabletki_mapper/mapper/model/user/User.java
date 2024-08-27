package com.tabletki_mapper.mapper.model.user;

import com.tabletki_mapper.mapper.model.UserDB;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {
    String username;
    String password;
    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public static UserDetails getUser(UserDB userDb) {
        return User.builder()
                .username(userDb.getUsername())
                .password(userDb.getPassword())
                .role(userDb.getRole().equals("SHOP") ? Role.SHOP : Role.ADMIN)
                .build();
    }

}
