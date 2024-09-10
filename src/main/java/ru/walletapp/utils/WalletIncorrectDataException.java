package ru.walletapp.utils;

public class WalletIncorrectDataException extends RuntimeException {
    public WalletIncorrectDataException(String message) {
        super(message);
    }
}
