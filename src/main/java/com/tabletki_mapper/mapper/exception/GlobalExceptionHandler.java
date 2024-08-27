package com.tabletki_mapper.mapper.exception;

import com.tabletki_mapper.mapper.model.nomenklatura.NomenklaturaResponseObject;
import com.tabletki_mapper.mapper.model.remains.RemainsResponseObject;
import com.tabletki_mapper.mapper.model.rests.RestsResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<?>> handleWebInputException(ServerWebInputException ex, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(getResponse(ex, exchange), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Mono<ResponseEntity<?>> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException ex, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(getResponse(ex, exchange), HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<?>> handleGeneralException(Exception ex, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(getResponse(ex, exchange), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NoAuthHeaderexception.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ResponseEntity<?>> handleAuthException(Exception ex, ServerWebExchange exchange) {
        return Mono.just(new ResponseEntity<>(getResponse(ex, exchange), HttpStatus.UNAUTHORIZED));
    }

    private Object getResponse(Exception ex, ServerWebExchange exchange) {
        String requestDescription = exchange.getRequest().getPath().toString();
        return switch (getControllerPath(requestDescription)) {
            case "1" -> getNomenklaturaErrorMessage(ex);
            case "2" -> getRemainsErrorObject(ex);
            case "3" -> getRestsErrorObject(ex);
            default -> ex.getMessage();
        };
    }

    private RestsResponseObject getRestsErrorObject(Exception ex) {
        return RestsResponseObject.builder()
                .isError(true)
                .errorMessage(trimMessage(ex.getMessage()))
                .branchResults(null)
                .build();
    }

    private RemainsResponseObject getRemainsErrorObject(Exception ex) {
        return RemainsResponseObject.builder()
                .isError(true)
                .errorMessage(trimMessage(ex.getMessage()))
                .branchResults(null)
                .build();
    }

    private NomenklaturaResponseObject getNomenklaturaErrorMessage(Exception ex) {
        return NomenklaturaResponseObject.builder()
                .isError(true)
                .errorMessage(trimMessage(ex.getMessage()))
                .offersCount(0)
                .isSupplierError(true)
                .supplierMessage(trimMessage(ex.getMessage()))
                .suppliersCount(0)
                .offersWithSupplierCodesCount(0)
                .build();
    }

    private String getControllerPath(String requestDescription) {
        if (requestDescription.matches("/v3/Import/Ref/.*")) {
            return "1";
        } else if (requestDescription.matches("/v3/Import/Remains")) {
            return "2";
        } else if (requestDescription.matches("/v3/Import/Rests")) {
            return "3";
        }
        return "";
    }

    private String trimMessage(String message) {
        return message.replaceAll("^.*?\\.[^:]+:\\s*", "").replaceAll("=.*|\\n.*", "").trim();
    }

}
