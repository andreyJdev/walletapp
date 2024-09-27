package ru.walletapp.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LowBalanceException extends RuntimeException {

    private final String message;

    public LowBalanceException(String message) {
        this.message = message;
    }
}
