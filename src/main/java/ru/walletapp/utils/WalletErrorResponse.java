package ru.walletapp.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class WalletErrorResponse {
    private String message;
    private LocalDateTime timestamp;
}
