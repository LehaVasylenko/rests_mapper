package com.tabletki_mapper.mapper.exception;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
public class NoAuthHeaderexception extends RuntimeException {
    public NoAuthHeaderexception(String message) {
        super(message);
    }

    public NoAuthHeaderexception(Throwable cause) {
        super(cause);
    }

}
