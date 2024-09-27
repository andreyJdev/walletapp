package ru.walletapp.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidJsonException extends RuntimeException{

    private final String message;

    public InvalidJsonException(String message) {
        this.message = message;
    }
}
