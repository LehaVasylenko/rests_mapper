package com.tabletki_mapper.mapper.config;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 13.08.2024
 */

import com.tabletki_mapper.mapper.dto.DrugEntityDTO;
import com.tabletki_mapper.mapper.model.ShopPoint;
import com.tabletki_mapper.mapper.model.UserDB;
import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.rests.Rests;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Bean
    public ReactiveRedisTemplate<String, UserDB> reactiveRedisUserTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, UserDB> serializationContext = RedisSerializationContext
                .<String, UserDB>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(UserDB.class))
                .hashKey(new Jackson2JsonRedisSerializer<>(Integer.class))
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    @Bean
    public ReactiveRedisTemplate<String, DrugEntityDTO> reactiveRedisDrugTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, DrugEntityDTO> serializationContext = RedisSerializationContext
                .<String, DrugEntityDTO>newSerializationContext(new GenericJackson2JsonRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(DrugEntityDTO.class))
                .hashKey(new Jackson2JsonRedisSerializer<>(Integer.class))
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    public ReactiveRedisTemplate<String, ShopPoint> reactiveRedisShopTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, ShopPoint> serializationContext = RedisSerializationContext
                .<String, ShopPoint>newSerializationContext(new GenericJackson2JsonRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(ShopPoint.class))
                .hashKey(new Jackson2JsonRedisSerializer<>(Integer.class))
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    public ReactiveRedisTemplate<String, Nomenklatura> reactiveRedisNomenklaturaTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, Nomenklatura> serializationContext = RedisSerializationContext
                .<String, Nomenklatura>newSerializationContext(new GenericJackson2JsonRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(Nomenklatura.class))
                .hashKey(new Jackson2JsonRedisSerializer<>(Integer.class))
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    public ReactiveRedisTemplate<String, Rests> reactiveRedisRestsTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, Rests> serializationContext = RedisSerializationContext
                .<String, Rests>newSerializationContext(new GenericJackson2JsonRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(Rests.class))
                .hashKey(new Jackson2JsonRedisSerializer<>(Integer.class))
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }


}
