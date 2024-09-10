package ru.walletapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class WalletResponseDTO {
    private UUID walletId;
    private BigDecimal amount;

    public WalletResponseDTO(UUID walletId, BigDecimal amount) {
        this.walletId = walletId;
        this.amount = amount;
    }
}
