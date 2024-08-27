package com.tabletki_mapper.mapper.exception;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 21.08.2024
 */
public class BadJsonException extends RuntimeException {
    public BadJsonException(String message) {
        super(message);
    }
}
