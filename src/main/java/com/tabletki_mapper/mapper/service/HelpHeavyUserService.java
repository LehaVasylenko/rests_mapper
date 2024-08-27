package com.tabletki_mapper.mapper.service;

import com.tabletki_mapper.mapper.dto.HeaderRequestDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Base64;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 07.08.2024
 */
@Service
public class HelpHeavyUserService {

    public Mono<String> getHeader(HeaderRequestDTO headerRequestDTO) {
        String auth = headerRequestDTO.getLogin() + ":" + headerRequestDTO.getPassword();
        String base64Auth = Base64
                .getEncoder()
                .encodeToString(auth.getBytes());
        return Mono.just("Basic " + base64Auth);
    }
}
