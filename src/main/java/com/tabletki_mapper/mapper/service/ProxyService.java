package com.tabletki_mapper.mapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tabletki_mapper.mapper.exception.BadJsonException;
import com.tabletki_mapper.mapper.model.Event;
import com.tabletki_mapper.mapper.model.nomenklatura.Nomenklatura;
import com.tabletki_mapper.mapper.model.nomenklatura.NomenklaturaResponseObject;
import com.tabletki_mapper.mapper.model.rests.Rests;
import com.tabletki_mapper.mapper.model.rests.RestsResponseObject;
import com.tabletki_mapper.mapper.repository.EventRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 21.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProxyService {

    NomenklaturaResponseService nomenklaturaService;
    RestsResponseService restsService;

    EventRepository eventRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    public Mono<NomenklaturaResponseObject> logAndProcessNomenklatura(String nomenklaturaString,
                                                                      String branchId,
                                                                      UserDetails userDetails,
                                                                      ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Event event = startEvent(exchange, headers);
        checkHeaders(headers, event);
        try {
            Nomenklatura nom = objectMapper.readValue(nomenklaturaString, Nomenklatura.class);
            saveGoodEvent(nomenklaturaString, event);
            return nomenklaturaService.processNomenklatura(nom, branchId, userDetails.getUsername());
        } catch (JsonMappingException e) {
            saveBadEvent(event, e.getMessage());
            throw new BadJsonException(e.getMessage());
        } catch (JsonProcessingException e) {
            saveBadEvent(event, e.getMessage());
            throw new BadJsonException(e.getMessage());
        }
    }

    public Mono<RestsResponseObject> loadAndProcessRests(String restsString,
                                                         UserDetails userDetails,
                                                         ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Event event = startEvent(exchange, headers);
        checkHeaders(headers, event);
        try {
            Rests rests = objectMapper.readValue(restsString, Rests.class);
            saveGoodEvent(restsString, event);
            return restsService.processRests(rests, userDetails.getUsername());
        } catch (JsonMappingException e) {
            saveBadEvent(event, e.getMessage());
            throw new BadJsonException(e.getMessage());
        } catch (JsonProcessingException e) {
            saveBadEvent(event, e.getMessage());
            throw new BadJsonException(e.getMessage());
        }

    }

    private void checkHeaders(HttpHeaders headers, Event event) {
        try {
            if (!headers.getContentType().toString().toLowerCase().contains("json")) {
                String message = "'" + headers.getContentType().toString() + "' not allowed";
                saveBadEvent(event, message);
                throw new UnsupportedMediaTypeException(message);
            }
        } catch (NullPointerException e) {
            String message = "No Content-Type were found";
            saveBadEvent(event, message);
            throw new UnsupportedMediaTypeException(message);
        }
    }

    private Event startEvent(ServerWebExchange exchange, HttpHeaders headers) {
        return Event.builder()
                .time(LocalDateTime.now())
                .path(exchange.getRequest().getPath().toString())
                .headers(headers.toString())
                .build();
    }

    private void saveGoodEvent(String inputString, Event event) {
        event.setIsError(false);
        event.setLength(inputString.length());
        eventRepository.save(event).subscribe();
    }

    private void saveBadEvent(Event event, String message) {
        event.setErrorMessage(message);
        event.setIsError(true);
        eventRepository.save(event).subscribe();
    }

}
