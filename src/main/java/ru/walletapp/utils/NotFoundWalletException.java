package ru.walletapp.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotFoundWalletException extends RuntimeException {

    private final String message;

    public NotFoundWalletException(String message) {
        this.message = message;
    }
}
