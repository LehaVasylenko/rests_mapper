package com.tabletki_mapper.mapper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class KafkaConfiguration {

//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;

//    @Bean
//    public <T>ReactiveKafkaProducerTemplate<String, T> reactiveKafkaProducerTemplate(
//            KafkaProperties properties) {
//        Map<String, Object> props = properties.buildProducerProperties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        props.put("max.request.size", "1000000000");
//        props.put("buffer.memory", "1000663296");
//        props.put("compression.type", "gzip");
//        props.put("retries", "5");
//        props.put("request.timeout.ms", "120000");
//        props.put("batch.size", "1048576");
//        return new ReactiveKafkaProducerTemplate<String, T>(SenderOptions.create(props));
//    }

}

